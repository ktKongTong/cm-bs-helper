package io.ktlab.bshelper.data.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.data.Event
import io.ktlab.bshelper.data.RuntimeEventFlow
import io.ktlab.bshelper.data.api.BeatSaverAPI
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.UserPreferenceV2
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.model.vo.BSPlaylistVO
import io.ktlab.bshelper.platform.DBAdapter
import io.ktlab.bshelper.platform.StorageService
import io.ktlab.bshelper.utils.UnzipUtility
import io.ktlab.kown.KownDownloader
import io.ktlab.kown.model.DownloadListener
import io.ktlab.kown.model.DownloadTaskBO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import okio.FileSystem
import okio.Path.Companion.toPath

private val logger = KotlinLogging.logger {}
class DownloaderRepository(
    storageService: StorageService,
    private val playlistRepository: PlaylistRepository,
    private val bsAPI: BeatSaverAPI,
    private val mapRepository: FSMapRepository,
    private val runtimeEventFlow: RuntimeEventFlow,
    private val userPreferenceRepository: UserPreferenceRepository,
) {
    private lateinit var preference: UserPreferenceV2

    private val downloadJob = Job()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + downloadJob)

    init {
        repositoryScope.launch {
            userPreferenceRepository.getUserPreference().collect {
                preference = it
            }
        }
    }

    private val tmpPath = storageService.getTempDir()

    private val downloader =
        KownDownloader.new()
            .setRetryCount(3)
            .setMaxConcurrentDownloads(5)
            .setDatabaseEnabled(true)
            .setDataBaseDriver(DBAdapter.getDriver())
            .build()

    private fun onCompleteAction(targetPlaylist: IPlaylist): (DownloadTaskBO) -> Unit {
        return { task ->
            logger.debug { "executing onCompleteAction: ${task.taskId}" }
            val zipFile = task.dirPath.toPath().resolve(task.filename)
            val targetPath = targetPlaylist.getTargetPath().toPath().resolve(task.title)
            try {
                UnzipUtility.unzip(zipFile.toString(), targetPath.toString())
                FileSystem.SYSTEM.delete(zipFile)
                mapRepository.activeFSMapByMapId(task.relateEntityId!!, targetPlaylist.id)
            } catch (e: Exception) {
                e.printStackTrace()
                runtimeEventFlow.sendEvent(Event.ExceptionEvent(e))
            }

        }
    }

    fun downloadMap(
        targetPlaylist: IPlaylist,
        map: BSMapVO,
    ) {
        val title = map.getFilename()
        val timestamp = Clock.System.now().epochSeconds
        val callback = onCompleteAction(targetPlaylist)
        downloader.newRequestBuilder(map.getDownloadURL(), tmpPath.toString(), "$title.zip")
            .setTag("single__.__${timestamp}__.__${targetPlaylist.id}")
            .setRelateEntityId(map.map.mapId)
            .setTitle(title)
            .build()
            .let { downloader.enqueue(it, DownloadListener(onCompleted = callback)) }
    }

    fun batchDownloadMap(
        targetPlaylist: IPlaylist,
        mapList: List<BSMapVO>,
    ) {
        val timestamp = Clock.System.now().epochSeconds
        val tag = "batch__.__${timestamp}__.__${targetPlaylist.id}"
        mapList.map {
            val title = it.getFilename()
            downloader.newRequestBuilder(it.getDownloadURL(), tmpPath.toString(), "$title.zip")
                .setTag(tag)
                .setRelateEntityId(it.map.mapId)
                .setTitle(title)
                .setDownloadListener(DownloadListener(onCompleted = onCompleteAction(targetPlaylist)))
                .build()
        }.let {
            downloader.enqueue(it)
        }
    }

    suspend fun downloadMapByMapIds(
        targetPlaylist: IPlaylist,
        mapIdList: List<String>,
    ) {
        val timestamp = Clock.System.now().epochSeconds
        val tag = "batch.$timestamp.import.${targetPlaylist.id}"
        mapIdList.chunked(50).map {
            val maps = mapRepository.getBSMapByIds(it).values.toList()

            mapRepository.batchInsertBSMapAndFSMap(maps, targetPlaylist)
            batchDownloadMap(targetPlaylist, maps)
        }
    }

    suspend fun createPlaylistAndDownloadBSPlaylist(bsPlaylist: BSPlaylistVO) {
        // check if playlist exist
        if (playlistRepository.isPlaylistExist(bsPlaylist.title)) {
            val playlistId = preference.currentManageDir.toPath().resolve(bsPlaylist.title).toString()
            playlistRepository.getPlaylistById(playlistId).takeIf { it is Result.Success }?.let {
                val playlist = (it as Result.Success).data
                runtimeEventFlow.sendEvent(Event.MessageEvent("playlist ${playlist.title} exist, start download"))
                downloadBSPlaylist(playlist, bsPlaylist)
            }
            return
        }
        val res = playlistRepository.createNewPlaylist(bsPlaylist.playlist.name, bsPlaylist.playlist.id)
        when (res) {
            is Result.Success -> {
                when (val playlist = playlistRepository.getPlaylistById(res.data.id)) {
                    is Result.Success -> {
                        runtimeEventFlow.sendEvent(Event.MessageEvent("create playlist ${playlist.data.title} success"))
                        downloadBSPlaylist(playlist.data, bsPlaylist)
                    }
                    is Result.Error -> {
                        runtimeEventFlow.sendEvent(Event.ExceptionEvent(playlist.exception))
                    }
                }
            }
            is Result.Error -> {
                runtimeEventFlow.sendEvent(Event.ExceptionEvent(res.exception))
            }
        }
    }

    suspend fun downloadBSPlaylist(
        targetPlaylist: IPlaylist,
        bsPlaylist: BSPlaylistVO,
    ) {
        withContext(Dispatchers.IO) {
            val localIdsAsync =
                async {
                    mapRepository.getAllFSMapByPlaylistId(targetPlaylist.id).map { it.getID() }
                }
            val maps =
                async {
                    return@async playlistRepository.getPlaylistDetailAllMaps(bsPlaylist.id)
                }.await()
            val localIds = localIdsAsync.await()
            // 1. filter items exist in fs map
            // 1. filter items exist in download tasks
            val filterMaps = maps.filter { it.getID() !in localIds }
            playlistRepository.insertBSPlaylist(bsPlaylist)
            mapRepository.batchInsertBSMapAndFSMap(maps.map { it as BSMapVO }, targetPlaylist)
            filterMaps
                .map {
                    it as BSMapVO
                    val title = it.getFilename()
                    val timestamp = Clock.System.now().epochSeconds
//                val callback = onCompleteAction(targetPlaylist)
                    downloader.newRequestBuilder(it.getDownloadURL(), tmpPath.toString(), "$title.zip")
                        .setTag("playlist-${bsPlaylist.id}__.__${timestamp}__.__${targetPlaylist.id}")
                        .setRelateEntityId(it.getID())
                        .setTitle(title)
                        .setDownloadListener(DownloadListener(onCompleted = onCompleteAction(targetPlaylist)))
                        .build()
                }.let {
                    downloader.enqueue(it)
                }
        }
    }

    fun retry(downloadTask: IDownloadTask) {
        when (downloadTask) {
            is IDownloadTask.MapDownloadTask -> {
                val tag = downloadTask.downloadTaskModel.tag
                tag?.split("__.__")?.lastOrNull()

                    ?.let {
                        playlistRepository.getPlaylistById(it).takeIf { it is Result.Success }?.let {
                            val playlist = (it as Result.Success).data
                            downloader.retryById(
                                downloadTask.downloadTaskModel.taskId,
                                DownloadListener(onCompleted = onCompleteAction(playlist)),
                            )
                        }
                    }
            }
            is IDownloadTask.BatchDownloadTask -> {
                downloadTask.tag.split("__.__").lastOrNull()

                    ?.let {
                        playlistRepository.getPlaylistById(it).takeIf { it is Result.Success }?.let {
                            val playlist = (it as Result.Success).data
                            downloader.retryByTag(downloadTask.tag, DownloadListener(onCompleted = onCompleteAction(playlist)))
                        }
                    }
            }
            is IDownloadTask.PlaylistDownloadTask -> {
                downloadTask.tag.split("__.__").lastOrNull()

                    ?.let {
                        playlistRepository.getPlaylistById(it).takeIf { it is Result.Success }?.let {
                            val playlist = (it as Result.Success).data
                            downloader.retryByTag(downloadTask.tag, DownloadListener(onCompleted = onCompleteAction(playlist)))
                        }
                    }
            }
        }
    }

    fun cancel(downloadTask: IDownloadTask) {
        when (downloadTask) {
            is IDownloadTask.MapDownloadTask -> {
                downloader.cancelById(downloadTask.downloadTaskModel.taskId)
            }
            is IDownloadTask.BatchDownloadTask -> {
                downloader.cancelByTag(downloadTask.tag)
            }
            is IDownloadTask.PlaylistDownloadTask -> {
                downloader.cancelByTag(downloadTask.tag)
            }
        }
    }

    fun pause(downloadTask: IDownloadTask) {
        when (downloadTask) {
            is IDownloadTask.MapDownloadTask -> {
                downloader.pauseById(downloadTask.downloadTaskModel.taskId)
            }
            is IDownloadTask.BatchDownloadTask -> {
                downloader.pauseByTag(downloadTask.tag)
            }
            is IDownloadTask.PlaylistDownloadTask -> {
                downloader.pauseByTag(downloadTask.tag)
            }
        }
    }

    fun resume(downloadTask: IDownloadTask) {
        when (downloadTask) {
            is IDownloadTask.MapDownloadTask -> {
                downloader.resumeById(
                    downloadTask.downloadTaskModel.taskId,
                    DownloadListener(onCompleted = onCompleteAction(downloadTask.targetPlaylist)),
                )
            }
            is IDownloadTask.BatchDownloadTask -> {
                downloader.resumeByTag(downloadTask.tag, DownloadListener(onCompleted = onCompleteAction(downloadTask.targetPlaylist)))
            }
            is IDownloadTask.PlaylistDownloadTask -> {
                downloader.resumeByTag(downloadTask.tag, DownloadListener(onCompleted = onCompleteAction(downloadTask.targetPlaylist)))
            }
        }
    }

    fun remove(downloadTask: IDownloadTask) {
        when (downloadTask) {
            is IDownloadTask.MapDownloadTask -> {
//                downloader.removeById(downloadTask.downloadTaskModel.taskId)
            }
            is IDownloadTask.BatchDownloadTask -> {
//                downloader.removeByTag(downloadTask.tag)
            }
            is IDownloadTask.PlaylistDownloadTask -> {
//                downloader.removeByTag(downloadTask.tag)
            }
        }
    }

    fun getDownloadTaskFlow(): Flow<List<IDownloadTask>> =
        downloader
            .getAllDownloadTaskFlow()
//            .map { it.map { it.copyTask() } }
            .map outer@{
                try {
                    val mapIds = it.mapNotNull { it.relateEntityId }
                    val playlistIds = it.mapNotNull { it.tag?.split("__.__")?.lastOrNull() }.toSet().toList()
                    val bsPlaylistIds =
                        it
                            .filter { it.tag?.startsWith("playlist") ?: false }
                            .mapNotNull {
                                it.tag?.split("__.__")?.firstOrNull()?.split("-")?.lastOrNull()?.toInt()
                            }.toSet().toList()
                    val res =
                        runBlocking {
                            val asyncBsMaps = async { return@async mapRepository.getBSMapByIds(mapIds) }
                            val asyncFsPlaylists =
                                async {
                                    return@async playlistRepository.getFSPlaylistByIds(playlistIds).associateBy { it.id }
                                }
                            val asyncBsPlaylists =
                                async {
                                    return@async playlistRepository.getBSPlaylistByIds(bsPlaylistIds).associateBy { it.id }
                                }
                            val bsMaps = asyncBsMaps.await()
                            val fsPlaylists = asyncFsPlaylists.await()
                            val bsPlaylists = asyncBsPlaylists.await()
                            val mapDownloadTaskList =
                                it.map {
                                    val targetPlaylistId = it.tag!!.split("__.__").last()
                                    val playlist = fsPlaylists[targetPlaylistId]
                                    if (it.relateEntityId == null || playlist == null) return@map null
                                    IDownloadTask.MapDownloadTask(it, bsMaps[it.relateEntityId!!]!!, playlist)
                                }.filterNotNull()
                            return@runBlocking mapDownloadTaskList.groupBy {
                                it.downloadTaskModel.tag
                            }.flatMap inner@{
                                if (it.key?.startsWith("playlist") == true) {
                                    val fsPlaylistId = it.key!!.split("__.__").last()
                                    val bsPlaylistId = it.key!!.split("__.__").first().split("-").last()
                                    // todo: convertodb
                                    val res = bsPlaylists[bsPlaylistId]
                                    if (res != null) {
                                        return@inner listOf(
                                            IDownloadTask.PlaylistDownloadTask(
                                                res,
                                                it.key!!,
                                                it.value.sortedByDescending { it.downloadTaskModel.createdAt },
                                                fsPlaylists[fsPlaylistId]!!,
                                            ),
                                        )
                                    } else {
                                        return@inner it.value.sortedByDescending { it.downloadTaskModel.createdAt }
                                    }
                                } else if (it.key?.startsWith("batch") == true) {
                                    return@inner listOf(
                                        IDownloadTask.BatchDownloadTask(
                                            it.key!!,
                                            it.value.sortedByDescending { it.downloadTaskModel.title },
                                            fsPlaylists[it.key!!.split("__.__").last()]!!,
                                        ),
                                    )
                                } else {
                                    return@inner it.value.sortedByDescending { it.downloadTaskModel.createdAt }
                                }
                            }
                        }
                    val tmp =
                        res.sortedByDescending {
                            when (it) {
                                is IDownloadTask.MapDownloadTask -> it.downloadTaskModel.createdAt
                                is IDownloadTask.BatchDownloadTask -> it.taskList.first().downloadTaskModel.createdAt
                                is IDownloadTask.PlaylistDownloadTask -> it.taskList.first().downloadTaskModel.createdAt
                            }
                        }
                    return@outer tmp
                } catch (e: Exception) {
                    e.printStackTrace()
                    runtimeEventFlow.sendEvent(Event.ExceptionEvent(e))
                }
                return@outer listOf<IDownloadTask>()
            }

    fun clearHistory() {
        repeat(2) {
            downloader.clear()
        }
    }
}
