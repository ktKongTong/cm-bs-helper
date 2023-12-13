package io.ktlab.bshelper.repository

import io.ktlab.bshelper.api.BeatSaverAPI
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
import io.ktlab.bshelper.utils.NewFSPlaylist
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.selects.select
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import okio.FileSystem
import okio.Path.Companion.toPath

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

    private fun CoroutineScope.tick(){
        launch {
            while (true){
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
    private fun CoroutineScope.collectBufferedMapIdOrHashId(){
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
                                bsMapDTO.convertToMapDifficulties().forEach {diff->
                                    bsHelperDAO.mapDifficultyQueries.insert(diff)
                                }
                            }
                        }
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }
        }
    }

    private val scanStateV2 = MutableStateFlow(ScanStateV2())


    suspend fun scanSinglePlaylist(basePath: String)
//    : Flow<ScanStateV2> = flow
    {
        val playlist = bsHelperDAO.fSPlaylistQueries.selectByIds(listOf(basePath)).executeAsOneOrNull()
        if (playlist == null) {
            scanStateV2.update { it.copy(state = ScanStateEventEnum.SCAN_ERROR, totalDirCount = 1, message = "No Such Playlist") }
//            emit(scanStateV2.value)
        }
        val syncTimestamp = playlist!!.syncTimestamp

        bsHelperDAO.fSPlaylistQueries.updateSyncState(SyncStateEnum.SYNCING,Clock.System.now().epochSeconds,basePath)
        val oldMaps = bsHelperDAO.fSMapQueries.getAllFSMapByPlaylistId(basePath).executeAsList()


        val playlistPath = basePath.toPath()
        val mapDirs = FileSystem.SYSTEM.list(playlistPath)
        val newMap = mapDirs
            .filter {
                FileSystem.SYSTEM.metadata(it).lastModifiedAtMillis?.let { lastModifiedAtMillis->
                    Instant.fromEpochMilliseconds(lastModifiedAtMillis).epochSeconds > syncTimestamp
                }?:false
            }
        scanStateV2.update { it.copy(state = ScanStateEventEnum.SCANNING, totalDirCount = mapDirs.size) }
//        emit(scanStateV2.value)
        val fsPlaylist = MutableStateFlow(NewFSPlaylist(basePath,name=playlistPath.name))
        val playlistScanStateV2 = MutableStateFlow(PlaylistScanStateV2(
            playlistName = playlistPath.name,
            playlistPath = playlistPath.toString(),
            currentMapDir = "",
            fileAmount = mapDirs.size,
            errorStates = listOf(),
        ))
        scanStateV2.update { it.copy(state = ScanStateEventEnum.SCANNING, totalDirCount = mapDirs.size, playlistScanList = listOf(playlistScanStateV2)) }
//        emit(scanStateV2.value)
        mapDirs.forEach { mapDir ->
            scanStateV2.update {
                it.copy(
                    scannedMapCount = it.scannedMapCount + 1,
                    currentMapDir = mapDir.name
                )
            }
            playlistScanStateV2.update {
                it.copy(
                    currentMapDir = mapDir.name,
                    scannedFileAmount = it.scannedFileAmount + 1
                )
            }
//            emit(scanStateV2.value)
            if (!BSMapUtils.checkIfBSMap(mapDir)) {
                return@forEach
            }
            if (newMap.contains(mapDir)) {
                val extractedMapInfo = BSMapUtils.extractMapInfoFromDirV2(mapDir)
                handleExtractMapInfoAndInsertToDB(extractedMapInfo, fsPlaylist) {
//                    emit(scanStateV2.value)
                }
            }else {
                val fsMap = oldMaps.firstOrNull { it.playlistBasePath.toPath().resolve(it.dirName) == mapDir }
                if (fsMap != null) {
                    fsPlaylist.update { it.copy(mapAmount = it.mapAmount + 1) }
                    bsHelperDAO.fSMapQueries.insert(fsMap.copy(playlistId = fsPlaylist.value.id))
                }
//                emit(scanStateV2.value)
            }
            bsHelperDAO.fSPlaylistQueries.updateMapAmount(fsPlaylist.value.mapAmount,fsPlaylist.value.id)
            bsHelperDAO.fSPlaylistQueries.updateSyncState(SyncStateEnum.SYNCED, Clock.System.now().epochSeconds,basePath)
        }
    }

     fun scanPlaylist(basePath: String): Flow<ScanStateV2> = flow {
        val manageDir = basePath.toPath()
//        val scanStateV2 = MutableStateFlow(ScanStateV2())
        val playlistDirs = FileSystem.SYSTEM.list(manageDir)
        scanStateV2.update { it.copy(state = ScanStateEventEnum.SCANNING, totalDirCount = playlistDirs.size) }
        emit(scanStateV2.value)
        // create a custom playlist if not exist
        val playlist = MutableStateFlow(NewFSPlaylist(basePath,name= "Custom Top Playlist", topPlaylist = true))
        val customMapInfos = mutableListOf<IExtractedMapInfo>()
        playlistDirs.map {path->
            scanStateV2.update { it.copy(scannedDirCount = it.scannedDirCount + 1,currentPlaylistDir = path.name) }
            emit(scanStateV2.value)
            if(!FileSystem.SYSTEM.metadata(path).isDirectory) {
                return@map
            }
            if(BSMapUtils.checkIfBSMap(path)) {
                scanStateV2.update { it.copy(scannedMapCount = it.scannedMapCount + 1, currentMapDir = path.name) }
                emit(scanStateV2.value)
                val extractedMapInfo = BSMapUtils.extractMapInfoFromDirV2(path)
                customMapInfos.add(extractedMapInfo)
            }
            val subPlaylistPath = basePath.toPath().resolve(path.name)

            val fsPlaylist = MutableStateFlow(NewFSPlaylist(subPlaylistPath.toString(),name= path.name))
            val files = FileSystem.SYSTEM.listOrNull(path)
            val playlistScanStateV2 = MutableStateFlow(PlaylistScanStateV2(
                playlistName = subPlaylistPath.name,
                playlistPath = subPlaylistPath.toString(),
                currentMapDir = "",
                fileAmount = files?.size?:0,
                errorStates = listOf(),
            ))
            scanStateV2.update { it.copy(
                playlistScanList = it.playlistScanList + playlistScanStateV2
            ) }
            files?.forEach { mapDir ->
                scanStateV2.update {
                    it.copy(
                    scannedMapCount = it.scannedMapCount + 1,
                    currentMapDir = mapDir.name)
                }
                playlistScanStateV2.update { it.copy(
                    currentMapDir = mapDir.name,
                    scannedFileAmount = it.scannedFileAmount + 1
                )}
                emit(scanStateV2.value)
                if(!BSMapUtils.checkIfBSMap(mapDir)) {
                    return@forEach
                }
                val extractedMapInfo = BSMapUtils.extractMapInfoFromDirV2(mapDir)
                handleExtractMapInfoAndInsertToDB(extractedMapInfo,fsPlaylist) { emit(scanStateV2.value) }
            }
            bsHelperDAO.fSPlaylistQueries.insertAnyway(fsPlaylist.value)
        }
        for (mapInfo in customMapInfos) {
            handleExtractMapInfoAndInsertToDB(mapInfo,playlist) {
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
        fsPlaylist:MutableStateFlow<FSPlaylist>,
        crossinline updateCallback: suspend (ScanStateV2) -> Unit
    ) {
        when(extractedMapInfo){
            is IExtractedMapInfo.LocalMapInfo -> {
                val diffDBOList = extractedMapInfo.generateMapDifficultyInfo()
                bsHelperDAO.transaction {
                    bsHelperDAO.fSMapQueries.insert(extractedMapInfo.generateFSMapDBO(fsPlaylist.value.id))
                    diffDBOList.forEach { bsHelperDAO.mapDifficultyQueries.insert(it) }
                }
                fsPlaylist.update { it.copy(mapAmount = it.mapAmount+1) }
            }
            is IExtractedMapInfo.BSMapInfo -> {
                val diffDBOList = extractedMapInfo.generateMapDifficultyInfo()
                bsHelperDAO.transaction {
                    bsHelperDAO.fSMapQueries.insert(extractedMapInfo.generateFSMapDBO(fsPlaylist.value.id))
                    diffDBOList.forEach { bsHelperDAO.mapDifficultyQueries.insert(it) }
                }
                // send to server to get map info
                mapIdOrHashIdChannel.send(extractedMapInfo.hash)
                fsPlaylist.update { it.copy(mapAmount = it.mapAmount+1) }
            }
            is IExtractedMapInfo.ErrorMapInfo -> {
                when(extractedMapInfo.exception){
                    is ScannerException.JSONFileTooLargeException -> {
                        bsHelperDAO.transaction {
                            bsHelperDAO.fSMapQueries.insert(extractedMapInfo.generateFSMapDBO(fsPlaylist.value.id))
                        }
                        mapIdOrHashIdChannel.send(extractedMapInfo.hash)
                        fsPlaylist.update { it.copy(mapAmount = it.mapAmount+1) }
                    }
                    is ScannerException.FileMissingException,is ScannerException.ParseException -> {
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