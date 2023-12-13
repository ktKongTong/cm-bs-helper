package io.ktlab.bshelper.repository

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.ktlab.bshelper.api.BeatSaverAPI
import io.ktlab.bshelper.model.*
import io.ktlab.bshelper.model.dto.request.MapFilterParam
import io.ktlab.bshelper.model.dto.response.APIRespResult
import io.ktlab.bshelper.model.dto.response.BSMapReviewDTO
import io.ktlab.bshelper.model.mapper.mapToVO
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.model.vo.FSPlaylistVO
import io.ktlab.bshelper.paging.BSMapPagingSource
import io.ktlab.bshelper.paging.BSPlaylistDetailPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class FSMapRepository(
    private val bsHelperDAO:BSHelperDatabase,
    private val bsAPI: BeatSaverAPI
) {
    fun getFlowMapsByPlaylistId(playlistId: String): Flow<Result<List<IMap>>> = flow {
        try {
            bsHelperDAO.fSMapQueries.getAllByPlaylistId(playlistId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .collect { q ->
                    val grouped = q.mapToVO()
                    emit(Result.Success(grouped))
                }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun moveFSMapsToPlaylist(targetPlaylist: IPlaylist, fsMaps:List<FSMap>): Flow<Result<String>> = flow {
        try {
            val playlistPath = (targetPlaylist as FSPlaylistVO).basePath
//            TODO failure handle
            fsMaps.forEach{
                val path = Path(it.playlistBasePath,it.dirName)
                val tarPath = Path(playlistPath,it.dirName)
                path.toFile().renameTo(tarPath.toFile())
            }
            bsHelperDAO.transaction {
                val targetPath = Path(targetPlaylist._name).resolve(targetPlaylist._name).pathString
                fsMaps.forEach {
                    bsHelperDAO.fSMapQueries.moveFSMapToPlaylist(targetPlaylist.id,targetPath,it.mapId,it.playlistId)
                }
            }
            emit(Result.Success(""))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun deleteAll() {
        bsHelperDAO.transaction {
            bsHelperDAO.fSMapQueries.deleteAllFSMap()
//            bsHelperDAO.bSMapQueries.deleteAll()
//            bsHelperDAO.bSMapVersionQueries.deleteAll()
//            bsHelperDAO.bSUserQueries.deleteAll()
            bsHelperDAO.fSPlaylistQueries.deleteAll()
        }
    }

    suspend fun deleteLocalAll() {
//        fsMapDao.deleteAll()
    }

    suspend fun insertBSMap(
        bsMap: BSMapVO
    ) {
        bsHelperDAO.transaction {
            bsHelperDAO.bSMapQueries.insert(bsMap.map)
            bsHelperDAO.bSUserQueries.insert(bsMap.uploader)
            bsMap.versions.map {
                bsHelperDAO.bSMapVersionQueries.insert(it.version)
                it.diffs.map {
                    bsHelperDAO.mapDifficultyQueries.insert(it)
                }
            }
        }
    }
    suspend fun insertFSMap(
        bsMap: BSMapVO
    ) {
        bsHelperDAO.transaction {
            bsHelperDAO.bSMapQueries.insert(bsMap.map)
            bsHelperDAO.bSUserQueries.insert(bsMap.uploader)
            bsMap.versions.map {
                bsHelperDAO.bSMapVersionQueries.insert(it.version)
                it.diffs.map {
                    bsHelperDAO.mapDifficultyQueries.insert(it)
                }
            }
        }
    }
    fun batchInsertBSMap(
        bsMaps: List<BSMapVO>
    ) {

        bsHelperDAO.transaction {
            bsMaps.map {
                bsHelperDAO.bSMapQueries.insert(it.map)
                bsHelperDAO.bSUserQueries.insert(it.uploader)
                it.versions.map {
                    bsHelperDAO.bSMapVersionQueries.insert(it.version)
                    it.diffs.map {
                        bsHelperDAO.mapDifficultyQueries.insert(it)
                    }
                }
            }
        }
    }
    fun batchInsertBSMapAndFSMap(
        bsMaps: List<BSMapVO>,
        targetPlaylist: IPlaylist
    ) {
        bsHelperDAO.transaction {
            bsMaps.map {
                bsHelperDAO.bSMapQueries.insert(it.map)
                bsHelperDAO.bSUserQueries.insert(it.uploader)
                it.versions.map {
                    bsHelperDAO.bSMapVersionQueries.insert(it.version)
                    it.diffs.map {
                        bsHelperDAO.mapDifficultyQueries.insert(it)
                    }
                }
                bsHelperDAO.fSMapQueries.insert(
                    FSMap(
                        mapId = it.getID(),
                        name = it.getSongName(),
                        duration = it.map.duration.toDuration(DurationUnit.SECONDS),
                        bpm = it.map.bpm,
                        levelAuthorName = it.map.levelAuthorName,
                        songAuthorName = it.map.songAuthorName,
                        songName = it.map.songName,
                        songSubname = it.map.songSubname,
                        relativeInfoFilename = "",
                        relativeSongFilename = "",
                        relativeCoverFilename = "",
                        dirName = "${it.map.mapId} (${it.map.songName} - ${it.map.songAuthorName})".replace("/", " "),
                        previewStartTime = 0.0,
                        previewDuration = Duration.ZERO,
                        playlistBasePath = targetPlaylist.getTargetPath(),
                        hash = it.versions.firstOrNull()?.version?.hash?:"",
                        playlistId = targetPlaylist.id,
                        active = false,
                    )
                )
//                bsHelperDAO.fSMapQueries.insertFSMap(it.map.mapId,playlistId)
            }
        }
    }

    fun activeFSMapByMapId(mapId: String,playlistId: String) {
        bsHelperDAO.transaction {
            bsHelperDAO.fSMapQueries.acitveFSMap(mapId,playlistId)
        }
    }
    fun getAllFSMapByPlaylistId(playlistId: String): List<IMap> {
        return bsHelperDAO.fSMapQueries.getAllByPlaylistId(playlistId)
            .executeAsList()
            .mapToVO()
    }
    fun getPagingBSMapByPlaylistId(playlistId: String): Flow<PagingData<IMap>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                BSPlaylistDetailPagingSource(bsAPI,playlistId)
            }
        ).flow
    }
    fun getPagingBSMap(mapFilterParam: MapFilterParam): Flow<PagingData<IMap>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                BSMapPagingSource(bsAPI,mapFilterParam)
            }
        ).flow
    }
    suspend fun deleteFSMapsByPath(playlistId: String,fsMaps:List<FSMap>):Result<String>{
        try {
//            批量删除
            fsMaps.forEach{
                FileSystem.SYSTEM.deleteRecursively(it.playlistBasePath.toPath().resolve(it.dirName))
            }
            val mapIds = fsMaps.map { it.mapId }
            bsHelperDAO.fSMapQueries.deleteFSMapByMapIdsAndPlaylistId(mapIds,playlistId)
            bsHelperDAO.fSPlaylistQueries.adjustPlaylistMapCntByPlaylistId((-mapIds.count()),playlistId)
        } catch (e: Exception) {
            return Result.Error(e)
        }
        return Result.Success("")
    }

    fun getLocalMapIdSet(): Flow<Set<Pair<String,String>>> = bsHelperDAO.fSMapQueries
        .getAllFSMapId()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map {
            it.map {item-> Pair(item.playlistId,item.mapId) }.toSet()
        }
        .catch {
//            Log()
            emit(setOf())
        }
    fun getLocalMapIdSetByPlaylist(playlistId: String): Flow<Set<Pair<String,String>>> = bsHelperDAO.fSMapQueries
        .getAllFSMapId()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map {
            it.map {item-> Pair(item.playlistId,item.mapId) }.toSet()
        }
        .catch {
//            Log()
            emit(setOf())
        }

    suspend fun getBSMapByIds(ids: List<String>): Map<String, BSMapVO> {
        val localMap = bsHelperDAO.bSMapQueries.selectAllByMapIds(ids).executeAsList().mapToVO().map {
            it.map.mapId to it
        }.toMap()
        val bsMapVOs = ids.filter { !localMap.containsKey(it) }.chunked(50).map {
            return@map bsAPI.getMapsByIds(it).values.map { it.convertToVO() }
        }.flatten()
        batchInsertBSMap(bsMapVOs)
        return bsMapVOs.map { it.map.mapId to it }.toMap() + localMap
    }
    suspend fun getBSMapReviewsById(mapId: String): APIRespResult<List<BSMapReviewDTO>> {
        return bsAPI.getMapReview(mapId)
    }

}