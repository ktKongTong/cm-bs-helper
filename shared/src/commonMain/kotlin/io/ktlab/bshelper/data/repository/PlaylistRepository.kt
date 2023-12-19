package io.ktlab.bshelper.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.ktlab.bshelper.data.PlaylistScannerV2
import io.ktlab.bshelper.data.api.BeatSaverAPI
import io.ktlab.bshelper.data.api.ToolAPI
import io.ktlab.bshelper.model.BSHelperDatabase
import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.UserPreferenceV2
import io.ktlab.bshelper.model.bsmg.BPList
import io.ktlab.bshelper.model.bsmg.BPListSong
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

class PlaylistRepository(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val bsHelperDAO: BSHelperDatabase,
    bsAPI: BeatSaverAPI,
    private val toolAPI: ToolAPI,
    private val bsAPIRepository: BSAPIRepository,
) {
    private val playlistJob = Job()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + playlistJob)

    private val playlistScanner = PlaylistScannerV2(bsHelperDAO, bsAPI)

    private lateinit var preference: UserPreferenceV2

    init {
        repositoryScope.launch {
            userPreferenceRepository.getUserPreference().collect {
                preference = it
            }
        }
    }

    fun isPlaylistExist(playlistName: String): Boolean {
        val manageDir = preference.currentManageDir.toPath()
        if (!FileSystem.SYSTEM.exists(manageDir)) {
            return false
        }
        return bsHelperDAO.fSPlaylistQueries.selectByIds(listOf(manageDir.resolve(playlistName).toString()))
            .executeAsList().firstOrNull()?.let { true } ?: false
    }

    // todo: EditPlaylist
    fun editPlaylist(playlist: FSPlaylist) {
        // if name changed, rename dir
        val manageDir = preference.currentManageDir.toPath()
        if (!FileSystem.SYSTEM.exists(manageDir)) {
            return
        }
        val oldDir = manageDir.resolve(playlist.name)
    }

    fun createNewPlaylist(
        playlistName: String,
        bsPlaylistId: Int? = null,
        description: String? = null,
        customTags: String? = null,
    ): Result<FSPlaylist> {
        val manageDir = preference.currentManageDir.toPath()
        if (!FileSystem.SYSTEM.exists(manageDir)) {
            return Result.Error(Exception("manage dir not exist"))
        }
        val safePlaylistName = playlistName.replace("[\\\\/:*?\"<>|]".toRegex(), "_")
        try {
            FileSystem.SYSTEM.createDirectory(manageDir.resolve(safePlaylistName))
        } catch (e: Exception) {
            return Result.Error(Exception("failed to create playlist dir ${e.message}"))
        }
        val basePath = manageDir.resolve(safePlaylistName).toString()
        val fSPlaylist =
            FSPlaylist(
                id = basePath,
                name = safePlaylistName,
                description = description ?: "custom create playlist",
                bsPlaylistId = bsPlaylistId,
                basePath = basePath,
                sync = SyncStateEnum.SYNCED,
                syncTimestamp = Clock.System.now().toEpochMilliseconds(),
                customTags = customTags,
                topPlaylist = false,
            )
        bsHelperDAO.fSPlaylistQueries.insertAnyway(fSPlaylist)
        return Result.Success(fSPlaylist)
    }

    fun insertBSPlaylist(bSPlaylist: BSPlaylistVO) {
        bsHelperDAO.transaction {
            bsHelperDAO.bSPlaylistQueries.insert(bSPlaylist.playlist)
            bsHelperDAO.bSUserQueries.insert(bSPlaylist.owner)
            if (bSPlaylist.curator != null) {
                bsHelperDAO.bSUserQueries.insert(bSPlaylist.curator)
            }
        }
    }

    fun deletePlaylistById(id: String) {
        bsHelperDAO.transaction {
            bsHelperDAO.fSPlaylistQueries.deleteById(id)
            bsHelperDAO.fSMapQueries.deleteFSMapByPlaylistId(id)
        }
    }

    fun clear() {
        bsHelperDAO.transaction {
            bsHelperDAO.fSMapQueries.deleteAllFSMap()
            bsHelperDAO.fSPlaylistQueries.deleteAll()
        }
    }

    // query
    fun getPlaylistById(id: String): Result<IPlaylist> =
        bsHelperDAO.fSPlaylistViewQueries.fSMapViewSelectByIds(listOf(id))
            .executeAsList().map {
                FSPlaylistVO.convertDBOToVO(it)
            }.firstOrNull()?.let {
                Result.Success(it)
            } ?: Result.Error(Exception("no such playlist with id:$id"))

    fun getFSPlaylistByIds(ids: List<String>): List<IPlaylist> =
        bsHelperDAO.fSPlaylistViewQueries.fSMapViewSelectByIds(ids)
            .executeAsList().map {
                FSPlaylistVO.convertDBOToVO(it)
            }


    fun getBSPlaylistByIds(ids: List<Int>): List<IPlaylist> =
        bsHelperDAO.bSPlaylistQueries.selectByIds(ids)
            .executeAsList().mapToVO()

    fun getAllPlaylistByManageDir(manageDir: String): Flow<Result<List<IPlaylist>>> =
        bsHelperDAO.fSPlaylistViewQueries.fSMapViewSelectAllPlaylist()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map {
                Result.Success(it.map { dbo -> FSPlaylistVO.convertDBOToVO(dbo) })
            }
            .catch {
                Result.Error(Exception(it.message))
            }

    fun getAllPlaylist(): Flow<Result<List<IPlaylist>>> =
        bsHelperDAO.fSPlaylistViewQueries.fSMapViewSelectAllPlaylist()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map {
                Result.Success(it.map { dbo -> FSPlaylistVO.convertDBOToVO(dbo) })
            }
            .catch {
                Result.Error(Exception(it.message))
            }

    // fs scan
    suspend fun scanSinglePlaylist(basePath: String) = playlistScanner.scanSinglePlaylist(basePath)

    fun scanPlaylist(basePath: String): Flow<ScanStateV2> = playlistScanner.scanPlaylist(basePath)


    // api ops
    suspend fun exportPlaylistAsKey(playlist: IPlaylist): Result<String> {
        val mapItems =
            bsHelperDAO
                .fSMapQueries
                .getAllByPlaylistId(playlist.id)
                .executeAsList()
                .mapToVO()
                .map { MapItem(it.fsMap.mapId) }
        val exportPlaylist = ExportPlaylist(playlist.title, mapItems)
        val res = toolAPI.setKV(KVSetRequest(value = exportPlaylist, timeout = 60 * 60 * 24 * 7))
        if (res is APIRespResult.Error) {
            return Result.Error(res.exception)
        }
        return Result.Success((res as APIRespResult.Success).data)
    }
    private val json = Json { prettyPrint = true;ignoreUnknownKeys = true }
    suspend fun exportPlaylistAsBPList(playlist: IPlaylist, targetDir:Path?=null): Result<String> {
        val mapItems =
            bsHelperDAO
                .fSMapQueries
                .getAllByPlaylistId(playlist.id)
                .executeAsList()
                .mapToVO()
                .map { BPListSong(
                    key = it.fsMap.mapId,
                    songName = "${it.fsMap.songName} - ${it.fsMap.songAuthorName}",
                    hash = it.fsMap.hash,
                ) }
        val p = (playlist as FSPlaylistVO)
        p.bsPlaylist?.playlist?.name
        val bpList = BPList(
            playlistAuthor = "custom created by BSHelper",
            playlistTitle = p.bsPlaylist?.playlist?.name ?: playlist.title,
            songs = mapItems,
        )
        val filename = "${playlist.title}.bplist"
        try {
            val dir = targetDir ?: preference.currentManageDir.toPath()
            FileSystem.SYSTEM.createDirectory(dir)
            FileSystem.SYSTEM.write(dir.resolve(filename)) {
                writeUtf8(json.encodeToString(BPList.serializer(), bpList))
            }
            return Result.Success(dir.resolve(filename).toString() + "has been created")
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun importPlaylistByKey(
        key: String,
        targetPlaylist: IPlaylist,
    ): Result<List<String>> {
        val res = toolAPI.getKV(key)
        return when (res) {
            is APIRespResult.Error -> {
                Result.Error(res.exception)
            }
            is APIRespResult.Success -> {
                val mapItems =
                    bsHelperDAO
                        .fSPlaylistQueries
                        .selectByIds(listOf(targetPlaylist.id))
                        .executeAsList()
                        .firstOrNull()?.let {
                            bsHelperDAO.fSMapQueries.getAllByPlaylistId(it.id).executeAsList().mapToVO()
                        } ?: emptyList()
                val ids =
                    res.data.mapItems.filter {
                        it.mapId !in mapItems.map { it.fsMap.mapId }
                    }.map { it.mapId }
                Result.Success(ids)
            }
        }
    }


    fun getPagingBSPlaylist(param: PlaylistFilterParam) = bsAPIRepository.getPagingBSPlaylist(param)

    fun getPlaylistDetailPagingMaps(playlistId: String) = bsAPIRepository.getPlaylistDetailPagingMaps(playlistId)

    fun getPagingBSUser() = bsAPIRepository.getPagingBSUser()

    suspend fun getBSUserDetail(id: Int) = bsAPIRepository.getBSUserDetail(id)

    suspend fun getPlaylistDetailAllMaps(playlistId: String) = bsAPIRepository.getPlaylistDetailAllMaps(playlistId)
}
