package io.ktlab.bshelper.repository

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.ktlab.bshelper.api.BeatSaverAPI
import io.ktlab.bshelper.api.ToolAPI
import io.ktlab.bshelper.model.*
import io.ktlab.bshelper.model.dto.ExportPlaylist
import io.ktlab.bshelper.model.dto.MapItem
import io.ktlab.bshelper.model.dto.request.KVSetRequest
import io.ktlab.bshelper.model.dto.request.PlaylistFilterParam
import io.ktlab.bshelper.model.dto.response.APIRespResult
import io.ktlab.bshelper.model.enums.SyncStateEnum
import io.ktlab.bshelper.model.mapper.mapToVO
import io.ktlab.bshelper.model.vo.BSPlaylistVO
import io.ktlab.bshelper.model.vo.FSPlaylistVO
import io.ktlab.bshelper.model.vo.ScanStateV2
import io.ktlab.bshelper.paging.BSPlaylistDetailPagingSource
import io.ktlab.bshelper.paging.BSPlaylistPagingSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.datetime.Clock
import okio.FileSystem
import okio.Path.Companion.toPath

class PlaylistRepository(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val bsHelperDAO: BSHelperDatabase,
    private val bsAPI: BeatSaverAPI,
    private val toolAPI: ToolAPI,
){

    private val playlistJob = Job()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + playlistJob)

    private val playlistScanner = PlaylistScannerV2(bsHelperDAO,bsAPI)

    private lateinit var preference: UserPreference
    private val  mutex = Mutex()


    init {
        repositoryScope.launch {
            userPreferenceRepository.getUserPreference().collect {
                preference = it
            }
        }
    }

    fun createNewPlaylist(playlistName:String): Result<String> {
        val manageDir = preference.currentManageDir.toPath()
        if (!FileSystem.SYSTEM.exists(manageDir)) {
            return Result.Error(Exception("manage dir not exist"))
        }
        try {
            FileSystem.SYSTEM.createDirectory(manageDir.resolve(playlistName))
        }catch (e:Exception){
            return Result.Error(Exception("create playlist dir failed, playlist name already exist"))
        }
        val basePath = manageDir.resolve(playlistName).toString()
        val fSPlaylist = FSPlaylist(
            id = basePath,
            name = playlistName,
            description = "custom create playlist",
            mapAmount = 0,
            maxNote = 0,
            avgNote = 0.0,
            avgObstacle = 0.0,
            avgBomb = 0.0,
            maxNps = 0.0,
            avgNps = 0.0,
            totalDuration = 0,
            maxDuration = 0,
            avgDuration = 0,
            bsPlaylistId = "",
            basePath = basePath,
            sync = SyncStateEnum.SYNCED,
            syncTimestamp = Clock.System.now().toEpochMilliseconds(),
            customTags = "",
            topPlaylist = false,
        )
        bsHelperDAO.fSPlaylistQueries.insertAnyway(fSPlaylist)
        return Result.Success(basePath)
    }

    fun deletePlaylistById(id:String) {
        bsHelperDAO.transaction{
            bsHelperDAO.fSPlaylistQueries.deleteById(id)
            bsHelperDAO.fSMapQueries.delteFSMapByPlaylistId(id)
        }
    }

    fun adjustPlaylistMapCntByPlaylistId(playlistId:String,amount:Int = 1){
        runBlocking {
            mutex.lock()
            bsHelperDAO.fSPlaylistQueries.transaction {
                bsHelperDAO.fSPlaylistQueries.adjustPlaylistMapCntByPlaylistId(amount,playlistId)
            }
            mutex.unlock()
        }
    }

    fun getPlaylistById(id:String): Result<IPlaylist> = bsHelperDAO.fSPlaylistQueries.selectByIds(listOf(id))
        .executeAsList().map {
            FSPlaylistVO.convertDBOToVO(it)
        }.firstOrNull()?.let {
            Result.Success(it)
        }?: Result.Error(Exception("no such playlist with id:$id"))

    fun getFSPlaylistByIds(ids :List<String>): List<IPlaylist> = bsHelperDAO.fSPlaylistQueries.selectByIds(ids)
        .executeAsList().map {
            FSPlaylistVO.convertDBOToVO(it)
        }

    fun insertBSPlaylist(bSPlaylist: BSPlaylistVO) {
        bsHelperDAO.transaction {
            bsHelperDAO.bSPlaylistQueries.insert(bSPlaylist.playlist)
            bsHelperDAO.bSUserQueries.insert(bSPlaylist.owner)
            if (bSPlaylist.curator != null) {
                bsHelperDAO.bSUserQueries.insert(bSPlaylist.curator!!)
            }
        }
    }

    fun getBSPlaylistByIds(ids :List<Int>): List<IPlaylist> = bsHelperDAO.bSPlaylistQueries.selectByIds(ids)
        .executeAsList().mapToVO()
    suspend fun getBSPlaylistById(id :String): Result<IPlaylist> = withContext(Dispatchers.IO){
        return@withContext try {
             Result.Success((bsAPI.getPlaylistDetail(id) as APIRespResult.Success).data.playlist.convertToVO())
        }catch (e:Exception){
            Result.Error(Exception("no such playlist with id:$id"))
        }
    }
//        bsHelperDAO.fSPlaylistQueries.selectByIds(ids)
//        .executeAsList().map {
//            FSPlaylistVO.convertDBOToVO(it)
//        }
    fun getAllPlaylistByManageDir(manageDir:String):Flow<Result<List<IPlaylist>>> = bsHelperDAO.fSPlaylistQueries.selectAll()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { Result.Success(it.map {dbo-> FSPlaylistVO.convertDBOToVO(dbo) }) }
        .catch {
            Result.Error(Exception(it.message))
        }

    fun getAllPlaylist(): Flow<Result<List<IPlaylist>>> = bsHelperDAO.fSPlaylistQueries.selectAll()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { Result.Success(it.map {dbo-> FSPlaylistVO.convertDBOToVO(dbo) }) }
        .catch {
            Result.Error(Exception(it.message))
        }

    fun getPagingBSPlaylist(playlistFilterParam: PlaylistFilterParam): Flow<PagingData<IPlaylist>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                BSPlaylistPagingSource(bsAPI,playlistFilterParam)
            }
        ).flow
    }

    suspend fun scanSinglePlaylist(basePath: String) = playlistScanner.scanSinglePlaylist(basePath)
     fun scanPlaylist(basePath: String): Flow<ScanStateV2> = playlistScanner.scanPlaylist(basePath)

//    suspend fun scanFSMapInPlaylists(scanState:ScanState): Flow<ScanState> = playlistScanner.scanFSMapInPlaylists(scanState)


    suspend fun exportPlaylistAsKey(playlist: IPlaylist): Result<String> {
         val mapItems = bsHelperDAO
             .fSMapQueries
             .getAllByPlaylistId(playlist.id)
             .executeAsList()
             .mapToVO()
             .map { MapItem(it.fsMap.mapId) }
             val exportPlaylist = ExportPlaylist(playlist.title,playlist.id,mapItems)
             val res = toolAPI.setKV(KVSetRequest(value = exportPlaylist, timeout = 60*60*24*7))
         if (res is APIRespResult.Error){
             return Result.Error(res.exception)
         }
         return Result.Success((res as APIRespResult.Success).data.key!!)
     }

    fun clear() {
        bsHelperDAO.transaction {
            bsHelperDAO.fSMapQueries.deleteAllFSMap()
            bsHelperDAO.fSPlaylistQueries.deleteAll()
        }
    }

    fun getPlaylistDetailPagingMaps(playlistId:String): Flow<PagingData<IMap>> {
        return Pager(
            config = PagingConfig(pageSize = 100, enablePlaceholders = false),
            pagingSourceFactory = {
                BSPlaylistDetailPagingSource(bsAPI,playlistId)
            }
        ).flow
    }

    // most less than 100, todo: need improve
    suspend fun getPlaylistDetailAllMaps(playlistId:String): List<IMap> {
        var page = 0
        val res = listOf<IMap>()
        val maps = bsAPI.getPlaylistDetail(playlistId, page).let { resp ->
            resp as APIRespResult.Success
            resp.data.maps.map { it.map.convertToVO() }
        }
        while (maps.size == 100) {
            page++
            val newMaps = bsAPI.getPlaylistDetail(playlistId, page).let { resp ->
                resp as APIRespResult.Success
                resp.data.maps.map { it.map.convertToVO() }
            }
            maps.plus(newMaps)
        }
        return maps
    }

    private fun <A, B>List<A>.pmap(f: suspend (A) -> B): List<B> = runBlocking {
        map { async(Dispatchers.Default) { f(it) } }.map { it.await() }
    }
}