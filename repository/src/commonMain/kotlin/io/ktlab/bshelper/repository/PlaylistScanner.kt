package io.ktlab.bshelper.repository

import io.ktlab.bshelper.api.BeatSaverAPI
import io.ktlab.bshelper.model.BSHelperDatabase
import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.model.MapDifficulty
import io.ktlab.bshelper.model.enums.ECharacteristic
import io.ktlab.bshelper.model.mapper.convertToBSMapDBO
import io.ktlab.bshelper.model.mapper.convertToBSMapVersionDBO
import io.ktlab.bshelper.model.mapper.convertToMapDifficulties
import io.ktlab.bshelper.model.vo.*
import io.ktlab.bshelper.utils.BSMapUtils
import io.ktlab.bshelper.utils.generateMapDifficultyInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.selects.select
import okio.IOException
import okio.Path.Companion.toPath
import java.io.File
import java.util.*

class PlaylistScanner (
    private val bsHelperDAO: BSHelperDatabase,
    private val bsAPI: BeatSaverAPI,
//    private val mapIdOrHashIdChannel: MutableStateFlow<String>,
) {
    private val playlistJob = Job()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + playlistJob)
    private val mapIdOrHashIdChannel = Channel<String>()
    private val mapIdOrHashIdBufferChannel = Channel<List<String>>()
    private val tickChannel = Channel<Unit>()

    init {
        repositoryScope.collectMapIdOrHashId()
        repositoryScope.tick()
        repositoryScope.collectBufferedMapIdOrHashId()
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
            }
        }
    }

    suspend fun scanPlaylist(basePath: String): Flow<ScanState> = flow{
        var scanState = ScanState.getDefaultInstance()
        try {
            val baseDir  = basePath.toPath().toFile()
            if (!baseDir.exists() or !baseDir.isDirectory){
                throw IOException("no such file or file not a directory:$basePath")
            }
            scanState = scanState.copy(state = GlobalScanStateEnum.SCANNING_PLAYLISTS)
            val playlists = mutableListOf<FSPlaylist>()
            val scanPlaylistDirs = mutableListOf<PlaylistScanState>()
            baseDir.listFiles()!!
//                .maxDepth(1)
                .forEach {
                    if (!it.isDirectory or BSMapUtils.checkIfBSMap(it)){
                        scanPlaylistDirs.add(
                            PlaylistScanState(
                                state = PlaylistScanStateEnum.SCAN_ERROR,
                                error = Error("seems not a playlist dir: ${it.name}. Maybe it's a file or a map dir"),
                                mapScanStates = mutableListOf(),
                            )
                        )
                        return@forEach
                    }

                    val mapFiles =it.list()
                    val uuid = UUID.randomUUID().toString()
                    val fsPlaylist = FSPlaylist(
                        uuid = uuid,
                        name = it.name,
                        description = "",
                        mapAmount = mapFiles?.size ?: 0,
                        maxNote = 0,
                        avgNote = 0.0,
                        avgObstacle = 0.0,
                        avgBomb = 0.0,
                        maxNps = 0.0,
                        avgNps = 0.0,
                        totalDuration = 0,
                        maxDuration = 0,
                        avgDuration = 0,
                        sync = false,
                        bsPlaylistId = "",
                        basePath = basePath,
                        syncTimestamp = System.currentTimeMillis(),
                    )
                    playlists.add(fsPlaylist)
                    scanPlaylistDirs.add(
                        PlaylistScanState(
                            state = PlaylistScanStateEnum.UNSELECTED,
                            playlistName = it.name,
                            playlistPath = basePath,
                            playlistId = uuid,
                            possibleMapAmount = it.list()?.size ?: 0,
                            mapScanStates = mutableListOf(),
                        )
                    )
                }
            scanState = scanState.copy(
                state = GlobalScanStateEnum.SCAN_PLAYLISTS_COMPLETE,
                playlistStates = scanPlaylistDirs.map { MutableStateFlow(it) }.toList(),
            )
            emit(scanState)
            bsHelperDAO.fSPlaylistQueries.transaction {
                playlists.forEach{
                    bsHelperDAO.fSPlaylistQueries.insertAll(it)
                }
            }
        }catch (e:Exception) {
            scanState = scanState.copy(state = GlobalScanStateEnum.SCAN_ERROR, error = Error(e.message))
            emit(scanState)
            return@flow
        }
    }

    suspend fun scanFSMapInPlaylists(scanState: ScanState): Flow<ScanState> = flow {
        val playlistStates = scanState.playlistStates.filter { it.value.state == PlaylistScanStateEnum.SELECTED_BUT_NOT_START }.toMutableList()

        emit(scanState.copy(state = GlobalScanStateEnum.SCANNING_MAPS))
        val ids = playlistStates.map { it.value.playlistId }
        val playlistToBeScanMap = bsHelperDAO.fSPlaylistQueries.selectByIds(ids)
            .executeAsList()
            .associateBy { it.uuid }

        playlistStates
            .map {
                scanDirAsPlaylist(it)
                playlistToBeScanMap[it.value.playlistId]?.let {playlist ->
                    bsHelperDAO.fSPlaylistQueries.insertAnyway(playlist.copy(sync = true,syncTimestamp = System.currentTimeMillis()))
                }
            }
        emit(scanState.copy(state = GlobalScanStateEnum.SCAN_COMPLETE))
    }

    private suspend fun scanDirAsPlaylist(
        playlistScanState: MutableStateFlow<PlaylistScanState>,
    ) {

        val playlistDirFile = playlistScanState.value.playlistPath.toPath().resolve(playlistScanState.value.playlistName).toFile()
        val mapScanStates = playlistDirFile.listFiles()!!
            .map {
                MutableStateFlow(
                    MapScanState(
                        state = ScanStateEnum.NOT_START,
                        mapPath = it.absolutePath,
                    )
                )
            }.toMutableList()
        val mapFiles = playlistDirFile.listFiles()
        playlistScanState.update { state ->
            state.copy(
                state = PlaylistScanStateEnum.SCANNING,
                mapScanStates = mapScanStates.map { it.value }.toList(),
            )
        }
        try {
            if (mapFiles!!.isNotEmpty()){
                mapFiles.forEachIndexed { idx,mapFile ->
                    val j = repositoryScope.async {
                        extractorOneMapInfo(playlistDirFile.absolutePath,
                            playlistScanState.value.playlistId,
                            mapFile,
                            mapScanStates[idx])
                    }
                    j.await()
                    playlistScanState.update {s-> s.copy(mapScanStates = mapScanStates.map { it.value }.toList()) }
                }
            }
        }catch (e:Exception){
            playlistScanState.update {
                it.copy(state = PlaylistScanStateEnum.SCAN_ERROR, error = Error(e.message))
            }
        }
    }

    private suspend fun extractorOneMapInfo(
        basePath: String,
        playlistId:String,
        mapFile: File,
        mapScanState: MutableStateFlow<MapScanState>,
    ){
        try {
            val extractedMapInfo = BSMapUtils.extractMapInfoFromDir(basePath, mapFile, playlistId)
            val difficultyDBOList = mutableListOf<MapDifficulty>()
            extractedMapInfo.mapInfo.difficultyBeatmapSets.forEach { bms->
                bms.difficultyBeatmaps.forEach { bf ->
                    // todo MapDifficulty
                    extractedMapInfo.v2MapObjectMap?.get(bms.characteristicName+bf.difficulty)?.let {
                        difficultyDBOList += it.generateMapDifficultyInfo(extractedMapInfo, ECharacteristic.from(bms.characteristicName),bf)
                    }
                    extractedMapInfo.v3MapObjectMap?.get(bms.characteristicName+bf.difficulty)?.let {
                        difficultyDBOList += it.generateMapDifficultyInfo(extractedMapInfo, ECharacteristic.from(bms.characteristicName),bf)
                    }
                }
            }
            bsHelperDAO.transaction {
                bsHelperDAO.fSMapQueries.insert(extractedMapInfo.generateFSMapDBO(playlistId))
                difficultyDBOList.forEach { bsHelperDAO.mapDifficultyQueries.insert(it) }
                bsHelperDAO.bSMapVersionQueries.insert(extractedMapInfo.generateVersionDBO())
            }
            mapIdOrHashIdChannel.send(extractedMapInfo.hash)
            mapScanState.update {
                it.copy(
                    state = ScanStateEnum.SCAN_COMPLETE,
                    mapName = extractedMapInfo.mapInfo.songName,
                    mapId = extractedMapInfo.mapId?:"",
                    mapVersion = extractedMapInfo.mapInfo.version,
                )
            }
        }catch (e:Exception){
            mapScanState.update {
                it.copy(state = ScanStateEnum.SCAN_ERROR, error = Error(e.message))
            }
            return
        }
    }

}