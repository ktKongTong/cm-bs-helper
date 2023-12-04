package io.ktlab.bshelper.repository

import io.ktlab.bshelper.api.BeatSaverAPI
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.model.vo.BSPlaylistVO
import io.ktlab.bshelper.service.DBAdapter
import io.ktlab.bshelper.service.StorageService
import io.ktlab.bshelper.utils.UnzipUtility
import io.ktlab.kown.KownDownloader
import io.ktlab.kown.model.DownloadListener
import io.ktlab.kown.model.DownloadTaskBO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import okio.FileSystem
import okio.Path.Companion.toPath

sealed interface IDownloadTask {
    val taskType: DownloadTaskType
    val targetPlaylist: IPlaylist
    data class MapDownloadTask(
        val downloadTaskModel:DownloadTaskBO,
        var relateMap:IMap,
        override val targetPlaylist: IPlaylist,
        override val taskType: DownloadTaskType = DownloadTaskType.Map,
    ):IDownloadTask {
        fun getProgress():Float{
            if (downloadTaskModel.totalBytes == 0L) return 0f
            return downloadTaskModel.downloadedBytes.toFloat()/downloadTaskModel.totalBytes.toFloat()
        }
    }
    data class BatchDownloadTask(
        val tag : String,
        val taskList: List<MapDownloadTask> = listOf(),
        override val targetPlaylist: IPlaylist,
        override val taskType: DownloadTaskType = DownloadTaskType.Batch,
    ):IDownloadTask {

    }
    data class PlaylistDownloadTask(
        var playlist:IPlaylist,
        val tag : String,
        val taskList: List<MapDownloadTask> = listOf(),
        override val targetPlaylist: IPlaylist,
        override val taskType: DownloadTaskType = DownloadTaskType.Playlist,
    ):IDownloadTask {
    }
}

enum class DownloadTaskType() {
    Map(),
    Batch(),
    Playlist(),
}


class DownloaderRepository(
    storageService: StorageService,
    private val playlistRepository: PlaylistRepository,
    private val bsAPI: BeatSaverAPI,
    private val mapRepository: FSMapRepository,
    private val runtimeEventFlow: RuntimeEventFlow
) {

    private val tmpPath = storageService.getTempDir()

    private val downloader = KownDownloader.new()
        .setRetryCount(3)
        .setMaxConcurrentDownloads(5)
        .setDatabaseEnabled(true)
        .setDataBaseDriver(DBAdapter.getDriver())
        .build()

    private fun onCompleteAction(targetPlaylist:IPlaylist): (DownloadTaskBO) -> Unit {
        return { task ->

            val zipFile = task.dirPath.toPath().resolve(task.filename)
            val targetPath = targetPlaylist.getTargetPath().toPath().resolve(task.title)
            UnzipUtility.unzip(zipFile.toString(), targetPath.toString())
            FileSystem.SYSTEM.delete(zipFile)
            playlistRepository.adjustPlaylistMapCntByPlaylistId(targetPlaylist.id)
            // add to fs map
            mapRepository.activeFSMapByMapId(task.relateEntityId!!,targetPlaylist.id)
        }
    }

    fun downloadMap(targetPlaylist: IPlaylist, map: BSMapVO) {
        val title = "${map.map.mapId} (${map.map.songName} - ${map.map.songAuthorName})".replace("/", " ")
        val timestamp = Clock.System.now()
        val callback = onCompleteAction(targetPlaylist)
        downloader.newRequestBuilder(map.getDownloadURL(), tmpPath.toString(), "$title.zip")
            .setTag(map.map.mapId)
            .setTag("single.$timestamp.${targetPlaylist.id}")
            .setRelateEntityId(map.map.mapId)
            .setTitle(title)
            .build()
            .let { downloader.enqueue(it, DownloadListener(onCompleted = callback)) }
    }

    fun batchDownloadMap(targetPlaylist: IPlaylist, mapList:List<BSMapVO>) {
        val timestamp = System.currentTimeMillis()
        val tag = "batch.$timestamp.${targetPlaylist.id}"
        mapList.map {
            val title = "${it.map.mapId} (${it.map.songName} - ${it.map.songAuthorName})".replace("/"," ")
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
    suspend fun downloadMapByHashes(targetPlaylist: IPlaylist, hashList: List<String>) {
        val timestamp = System.currentTimeMillis()
        val tag = "batch.$timestamp.${targetPlaylist.id}"
        hashList.chunked(50).map {
//            val res = bsAPI.getMapsByHashes(it)
//            res.map {
//                downloader.newRequestBuilder(it.value.getDownloadURL(), tmpPath.absolutePath, "")
//                    .setTag(tag)
//                    .setRelateEntityId(it.value.id)
//                    .title(it.value.name)
//                    .build()
//            }.map { downloader.enqueue(it) }
        }
    }

    suspend fun downloadMapByIds(targetPlaylist: IPlaylist, mapIds: List<String>) {
        mapIds.chunked(50).map {
            val res = bsAPI.getMapsByIds(it)
        }
    }

    fun downloadBSPlaylist(targetPlaylist: IPlaylist, bsPlaylist: BSPlaylistVO){

    }

    fun retry(downloadTask: IDownloadTask) {
        when(downloadTask) {
            is IDownloadTask.MapDownloadTask -> {
                val tag = downloadTask.downloadTaskModel.tag
                tag?.split(".")?.lastOrNull()?.let {
                    playlistRepository.getPlaylistById(it).takeIf { it is Result.Success }?.let {
                        val playlist = (it as Result.Success).data
                        downloader.retryById(downloadTask.downloadTaskModel.taskId, DownloadListener(onCompleted = onCompleteAction(playlist)))
                    }
                }
            }
            is IDownloadTask.BatchDownloadTask -> {
                downloadTask.tag.split(".").lastOrNull()?.let {
                    playlistRepository.getPlaylistById(it).takeIf { it is Result.Success }?.let {
                        val playlist = (it as Result.Success).data
                        downloader.retryByTag(downloadTask.tag, DownloadListener(onCompleted = onCompleteAction(playlist)))
                    }
                }
            }
            is IDownloadTask.PlaylistDownloadTask -> {

            }
        }

    }
    fun cancel(downloadTask: IDownloadTask) {
        when(downloadTask) {
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
        when(downloadTask) {
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
        when(downloadTask) {
            is IDownloadTask.MapDownloadTask -> {
                downloader.resumeById(downloadTask.downloadTaskModel.taskId, DownloadListener(onCompleted = onCompleteAction(downloadTask.targetPlaylist)))
            }
            is IDownloadTask.BatchDownloadTask -> {
                downloader.resumeByTag(downloadTask.tag)
            }
            is IDownloadTask.PlaylistDownloadTask -> {
                downloader.resumeByTag(downloadTask.tag)
            }
        }
    }

    fun remove(downloadTask: IDownloadTask) {
        when(downloadTask) {
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

    fun getDownloadTaskFlow(): Flow<List<IDownloadTask>> = downloader
        .getAllDownloadTaskFlow()
        .map { it.map { it.copyTask()  } }
        .map outer@{
            try {
                val mapIds = it.mapNotNull { it.relateEntityId }
                val playlistIds = it.mapNotNull { it.tag?.split(".")?.lastOrNull() }.toSet().toList()
                val res = runBlocking {
                    val asyncBsMaps = async { return@async mapRepository.getBSMapByIds(mapIds) }
                    val asyncBsPlaylists = async { return@async playlistRepository.getPlaylistByIds(playlistIds).associateBy { it.id } }
                    val bsMaps = asyncBsMaps.await()
                    val bsPlaylists = asyncBsPlaylists.await()
                    val mapDownloadTaskList = it.map {
                        val targetPlaylistId = it.tag!!.split(".").last()
                        val playlist = bsPlaylists[targetPlaylistId]
                        if (it.relateEntityId == null || playlist == null) return@map null
                        IDownloadTask.MapDownloadTask(it, bsMaps[it.relateEntityId!!]!!, playlist)
                    }.filterNotNull()
                    return@runBlocking mapDownloadTaskList.groupBy {
                        it.downloadTaskModel.tag
                    }.flatMap inner@ {
                        if (it.key?.startsWith("playlist") == true) {
                            val id = it.key!!.split(".").dropLast(1).last()
                            val res = playlistRepository.getPlaylistById(id)
                            when(res){
                                is Result.Success -> {
                                    return@inner listOf(IDownloadTask.PlaylistDownloadTask(
                                        res.data,
                                        it.key!!,
                                        it.value.sortedByDescending { it.downloadTaskModel.createdAt },
                                        bsPlaylists[it.key!!.split(".").last()]!!
                                    ))
                                }
                                else -> {
                                    return@inner it.value.sortedByDescending { it.downloadTaskModel.createdAt }
                                }
                            }
                        }else if(it.key?.startsWith("batch") == true) {
                            return@inner listOf(IDownloadTask.BatchDownloadTask(
                                it.key!!,it.value.sortedByDescending { it.downloadTaskModel.title },
                                bsPlaylists[it.key!!.split(".").last()]!!
                            ))
                        }else {
                            return@inner it.value.sortedByDescending { it.downloadTaskModel.createdAt }
                        }
                    }
                }
                val tmp = res.sortedByDescending {
                    when(it) {
                        is IDownloadTask.MapDownloadTask -> it.downloadTaskModel.createdAt
                        is IDownloadTask.BatchDownloadTask -> it.taskList.first().downloadTaskModel.createdAt
                        is IDownloadTask.PlaylistDownloadTask -> it.taskList.first().downloadTaskModel.createdAt
                    }
                }
                return@outer tmp
            }catch (e: Exception) {
                runtimeEventFlow.sendEvent(Event(EventType.Exception, e))
            }
            return@outer listOf<IDownloadTask>()
        }

    fun clearHistory() {
        repeat(2) {
            downloader.clear()
        }
    }

}