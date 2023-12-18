package io.ktlab.bshelper.data

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.data.api.BeatSaverAPI
import io.ktlab.bshelper.model.BSHelperDatabase
import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.model.enums.SyncStateEnum
import io.ktlab.bshelper.model.mapper.convertToBSMapDBO
import io.ktlab.bshelper.model.mapper.convertToBSMapVersionDBO
import io.ktlab.bshelper.model.mapper.convertToMapDifficulties
import io.ktlab.bshelper.model.scanner.IExtractedMapInfo
import io.ktlab.bshelper.model.scanner.ScannerException
import io.ktlab.bshelper.model.vo.PlaylistScanStateV2
import io.ktlab.bshelper.model.vo.ScanStateEventEnum
import io.ktlab.bshelper.model.vo.ScanStateV2
import io.ktlab.bshelper.utils.BSMapUtils
import io.ktlab.bshelper.utils.newFSPlaylist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import okio.FileSystem
import okio.Path.Companion.toPath

private val logger = KotlinLogging.logger {  }
class PlaylistScannerV2(
    private val bsHelperDAO: BSHelperDatabase,
    private val bsAPI: BeatSaverAPI,
) {
    private val scannerJob = Job()
    private val scannerScope = CoroutineScope(Dispatchers.IO + scannerJob)
    private val mapIdOrHashIdChannel = Channel<String>()
    private val mapIdOrHashIdBufferChannel = Channel<List<String>>()
    private val tickChannel = Channel<Unit>()

    init {
        scannerScope.collectMapIdOrHashId()
        scannerScope.tick()
        scannerScope.collectBufferedMapIdOrHashId()
    }

    private fun CoroutineScope.tick() {
        launch {
            while (true) {
                delay(30000)
                tickChannel.send(Unit)
            }
        }
    }

    // todo: need to improve
//    collect for channel , every 50 mapIdOrHashId collected,
//    or 30 seconds passed, send to server to get map info
    private fun CoroutineScope.collectMapIdOrHashId() {
        launch {
            var mapIdOrHashIdSet = mutableSetOf<String>()
            while (true) {
                select {
                    mapIdOrHashIdChannel.onReceive {
                        mapIdOrHashIdSet.add(it)
                        if (mapIdOrHashIdSet.size >= 50) {
                            mapIdOrHashIdBufferChannel.send(mapIdOrHashIdSet.toList())
                            mapIdOrHashIdSet = mutableSetOf()
                        }
                    }
                    tickChannel.onReceive {
                        if (mapIdOrHashIdSet.isNotEmpty()) {
                            mapIdOrHashIdBufferChannel.send(mapIdOrHashIdSet.toList())
                            mapIdOrHashIdSet = mutableSetOf()
                        }
                    }
                }
            }
        }
    }

    private fun CoroutineScope.collectBufferedMapIdOrHashId() {
        launch {
            mapIdOrHashIdBufferChannel.receiveAsFlow().collect {
                try {
                    val maps = bsAPI.getMapsByHashes(it)
                    maps
                        .filter { it.value != null }
                        .forEach { (_, bsMapDTO) ->
                            bsHelperDAO.transaction {
                                bsHelperDAO.bSMapQueries.insert(bsMapDTO!!.convertToBSMapDBO())
                                bsHelperDAO.bSUserQueries.insert(bsMapDTO.uploader.convertToEntity())
                                bsHelperDAO.bSMapVersionQueries.insert(bsMapDTO.convertToBSMapVersionDBO())
                                bsMapDTO.convertToMapDifficulties().forEach { diff ->
                                    bsHelperDAO.mapDifficultyQueries.insert(diff)
                                }
                            }
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private val scanStateV2 = MutableStateFlow(ScanStateV2())

    suspend fun scanSinglePlaylist(
        basePath: String,
        topPlaylist: Boolean = false,
    ) { //    : Flow<ScanStateV2> = flow
        val playlist = bsHelperDAO.fSPlaylistViewQueries.fSMapViewSelectByIds(listOf(basePath)).executeAsOneOrNull()
        if (playlist == null) {
            scanStateV2.update { it.copy(state = ScanStateEventEnum.SCAN_ERROR, totalDirCount = 1, message = "No Such Playlist") }
            return
        }
        // check if playlist still exist
        FileSystem.SYSTEM.exists(basePath.toPath()).let {
            // if not exist, delete playlist
            if (!it) {
                // send runtime event to notify playlist deleted
                bsHelperDAO.fSPlaylistQueries.deleteById(basePath)
                bsHelperDAO.fSMapQueries.deleteFSMapByPlaylistId(basePath)
//                if (topPlaylist) {
                // todo delete all sub playlist
//                }
                return
            }
        }

        val topPlaylist = playlist.playlist_name == "Custom Top Playlist"
        bsHelperDAO.fSPlaylistQueries.updateSyncState(SyncStateEnum.SYNCING, Clock.System.now().epochSeconds, basePath)

        val oldMaps = bsHelperDAO.fSMapQueries.getAllFSMapByPlaylistId(basePath).executeAsList()
        val playlistPath = basePath.toPath()
        val allDirs = FileSystem.SYSTEM.list(playlistPath)
        val changedDir =
            allDirs
                .filter {
                    FileSystem.SYSTEM.metadata(it).lastModifiedAtMillis?.let { lastModifiedAtMillis ->
                        Instant.fromEpochMilliseconds(lastModifiedAtMillis).epochSeconds > playlist.playlist_syncTimestamp
                    } ?: false
                }
        if (topPlaylist) {
            val oldPlaylists = bsHelperDAO.fSPlaylistQueries.selectByIds(listOf(basePath)).executeAsList()
            val deletedPlaylist = oldPlaylists.filter { !changedDir.map { it.name }.contains(it.name) }
            bsHelperDAO.fSPlaylistQueries.deleteByIds(deletedPlaylist.map { it.id })
            bsHelperDAO.fSMapQueries.deleteFSMapByPlaylistIds(deletedPlaylist.map { it.id })
            val addedPlaylist =
                changedDir.filter { !oldPlaylists.map { it.id }.contains(playlistPath.resolve(it.name).toString()) }
                    .filter { !BSMapUtils.checkIfBSMap(it) }
            addedPlaylist.forEach {
                val fsPlaylist =
                    MutableStateFlow(newFSPlaylist(basePath, name = it.name).copy(sync = SyncStateEnum.SYNCING, syncTimestamp = 0L))
                bsHelperDAO.fSPlaylistQueries.insertAnyway(fsPlaylist.value)
                scanSinglePlaylist(fsPlaylist.value.basePath)
            }
        }
        // remove not exist map
        val deletedMap =
            oldMaps.filter { oldMap ->
                allDirs.none { it == oldMap.playlistBasePath.toPath().resolve(oldMap.dirName) }
            }
        bsHelperDAO.fSMapQueries.deleteFSMapByMapPathsAndPlaylistId(deletedMap.map { it.dirName }, playlist.playlist_id)
        scanStateV2.update { it.copy(state = ScanStateEventEnum.SCANNING, totalDirCount = allDirs.size) }
        val fsPlaylist = MutableStateFlow(newFSPlaylist(basePath, name = playlist.playlist_name, topPlaylist = topPlaylist))
        val playlistScanStateV2 =
            MutableStateFlow(
                PlaylistScanStateV2(
                    playlistName = playlistPath.name,
                    playlistPath = playlistPath.toString(),
                    currentMapDir = "",
                    fileAmount = allDirs.size,
                    errorStates = listOf(),
                ),
            )
        scanStateV2.update {
            it.copy(state = ScanStateEventEnum.SCANNING, totalDirCount = allDirs.size, playlistScanList = listOf(playlistScanStateV2))
        }
        val existMap = mutableListOf<IExtractedMapInfo>()
        allDirs.forEach { mapDir ->
            scanStateV2.update {
                it.copy(
                    scannedMapCount = it.scannedMapCount + 1,
                    currentMapDir = mapDir.name,
                )
            }
            playlistScanStateV2.update {
                it.copy(
                    currentMapDir = mapDir.name,
                    scannedFileAmount = it.scannedFileAmount + 1,
                )
            }
//            emit(scanStateV2.value)
            if (!BSMapUtils.checkIfBSMap(mapDir)) {
                return@forEach
            }
            if (changedDir.contains(mapDir)) {
                val extractedMapInfo = BSMapUtils.extractMapInfoFromDirV2(mapDir)
                existMap.add(extractedMapInfo)
                handleExtractMapInfoAndInsertToDB(extractedMapInfo, fsPlaylist) {
                }
            } else {
                val fsMap = oldMaps.firstOrNull { it.playlistBasePath.toPath().resolve(it.dirName) == mapDir }
                if (fsMap != null) {
                    bsHelperDAO.fSMapQueries.insert(fsMap.copy(playlistId = fsPlaylist.value.id))
                }
            }
        }
        bsHelperDAO.fSPlaylistQueries.insertAnyway(fsPlaylist.value)
        bsHelperDAO.fSPlaylistQueries.updateSyncState(SyncStateEnum.SYNCED, Clock.System.now().epochSeconds, basePath)
    }

    fun scanPlaylist(basePath: String): Flow<ScanStateV2> =
        flow {
            val manageDir = basePath.toPath()
            val playlistDirs = FileSystem.SYSTEM.list(manageDir)
            scanStateV2.update { it.copy(state = ScanStateEventEnum.SCANNING, totalDirCount = playlistDirs.size) }
            emit(scanStateV2.value)
            // create a custom playlist if not exist
            val playlist = MutableStateFlow(newFSPlaylist(basePath, name = "Custom Top Playlist", topPlaylist = true))
            val customMapInfos = mutableListOf<IExtractedMapInfo>()
            playlistDirs.map { path ->
                scanStateV2.update { it.copy(scannedDirCount = it.scannedDirCount + 1, currentPlaylistDir = path.name) }
                emit(scanStateV2.value)
                if (!FileSystem.SYSTEM.metadata(path).isDirectory) {
                    return@map
                }
                if (BSMapUtils.checkIfBSMap(path)) {
                    logger.debug { "scanPlaylist: ${path.name} is a map" }
                    scanStateV2.update { it.copy(scannedMapCount = it.scannedMapCount + 1, currentMapDir = path.name) }
                    emit(scanStateV2.value)
                    val extractedMapInfo = BSMapUtils.extractMapInfoFromDirV2(path)
                    customMapInfos.add(extractedMapInfo)
                    return@map
                }

                logger.debug { "scanPlaylist: ${path.name} is a playlist" }
                val subPlaylistPath = basePath.toPath().resolve(path.name)

                val fsPlaylist = MutableStateFlow(newFSPlaylist(subPlaylistPath.toString(), name = path.name))
                val files = FileSystem.SYSTEM.listOrNull(path)
                val playlistScanStateV2 =
                    MutableStateFlow(
                        PlaylistScanStateV2(
                            playlistName = subPlaylistPath.name,
                            playlistPath = subPlaylistPath.toString(),
                            currentMapDir = "",
                            fileAmount = files?.size ?: 0,
                            errorStates = listOf(),
                        ),
                    )
                scanStateV2.update {
                    it.copy(
                        playlistScanList = it.playlistScanList + playlistScanStateV2,
                    )
                }
                files?.forEach { mapDir ->
                    scanStateV2.update {
                        it.copy(
                            scannedMapCount = it.scannedMapCount + 1,
                            currentMapDir = mapDir.name,
                        )
                    }
                    playlistScanStateV2.update {
                        it.copy(
                            currentMapDir = mapDir.name,
                            scannedFileAmount = it.scannedFileAmount + 1,
                        )
                    }
                    emit(scanStateV2.value)
                    if (!BSMapUtils.checkIfBSMap(mapDir)) {
                        return@forEach
                    }
                    val extractedMapInfo = BSMapUtils.extractMapInfoFromDirV2(mapDir)
                    handleExtractMapInfoAndInsertToDB(extractedMapInfo, fsPlaylist) { emit(scanStateV2.value) }
                }
                bsHelperDAO.fSPlaylistQueries.insertAnyway(fsPlaylist.value)
            }
            for (mapInfo in customMapInfos) {
                handleExtractMapInfoAndInsertToDB(mapInfo, playlist) {
                    emit(scanStateV2.value)
                }
            }
            bsHelperDAO.fSPlaylistQueries.insertAnyway(playlist.value)
            scanStateV2.update { it.copy(state = ScanStateEventEnum.SCAN_COMPLETE) }
            emit(scanStateV2.value)
            scanStateV2.update { ScanStateV2.getDefaultInstance() }
        }

    private suspend inline fun handleExtractMapInfoAndInsertToDB(
        extractedMapInfo: IExtractedMapInfo,
        fsPlaylist: MutableStateFlow<FSPlaylist>,
        crossinline updateCallback: suspend (ScanStateV2) -> Unit,
    ) {
        when (extractedMapInfo) {
            is IExtractedMapInfo.LocalMapInfo -> {
                val diffDBOList = extractedMapInfo.generateMapDifficultyInfo()
                bsHelperDAO.transaction {
                    bsHelperDAO.fSMapQueries.insert(extractedMapInfo.generateFSMapDBO(fsPlaylist.value.id))
                    diffDBOList.forEach { bsHelperDAO.mapDifficultyQueries.insert(it) }
                }
            }
            is IExtractedMapInfo.BSMapInfo -> {
                val diffDBOList = extractedMapInfo.generateMapDifficultyInfo()
                bsHelperDAO.transaction {
                    bsHelperDAO.fSMapQueries.insert(extractedMapInfo.generateFSMapDBO(fsPlaylist.value.id))
                    diffDBOList.forEach { bsHelperDAO.mapDifficultyQueries.insert(it) }
                }
                // send to server to get map info
                mapIdOrHashIdChannel.send(extractedMapInfo.hash)
            }
            is IExtractedMapInfo.ErrorMapInfo -> {
                when (extractedMapInfo.exception) {
                    is ScannerException.JSONFileTooLargeException -> {
                        bsHelperDAO.transaction {
                            bsHelperDAO.fSMapQueries.insert(extractedMapInfo.generateFSMapDBO(fsPlaylist.value.id))
                        }
                        mapIdOrHashIdChannel.send(extractedMapInfo.hash)
                    }
                    is ScannerException.FileMissingException, is ScannerException.ParseException -> {
                        scanStateV2.update { it.copy(errorStates = it.errorStates + extractedMapInfo.exception) }
                        scanStateV2.value.playlistScanList.last().update {
                            it.copy(errorStates = it.errorStates + extractedMapInfo.exception)
                        }
                        updateCallback(scanStateV2.value)
                    }
//                    is ScannerException.ParseException -> TODO()
                }
            }
        }
    }
}
