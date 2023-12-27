package io.ktlab.bshelper.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.data.api.BeatSaverAPI
import io.ktlab.bshelper.model.BSHelperDatabase
import io.ktlab.bshelper.model.FSMap
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.dto.request.MapFilterParam
import io.ktlab.bshelper.model.mapper.mapToVO
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.model.vo.FSPlaylistVO
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okio.FileSystem
import okio.Path.Companion.toPath

private val logger = KotlinLogging.logger{}

class FSMapRepository(
    private val bsHelperDAO: BSHelperDatabase,
    private val bsAPI: BeatSaverAPI,
    private val bsAPIRepository: BSAPIRepository
) {
    fun getFlowMapsByPlaylistId(playlistId: String): Flow<Result<List<IMap>>> =
        flow {
            logger.debug { "getFlowMapsByPlaylistId: playlistId$playlistId" }
            try {
                bsHelperDAO.fSMapQueries.getAllByPlaylistId(playlistId)
                    .asFlow()
                    .mapToList(Dispatchers.IO)
                    .collect { q ->
                        val grouped = q.mapToVO()
                        emit(Result.Success(grouped))
                    }
            } catch (e: Exception) {
                logger.error(e) { "getFlowMapsByPlaylistId: playlistId$playlistId, error: ${e.message}" }
                emit(Result.Error(e))
            }
        }

    fun moveFSMapsToPlaylist(
        targetPlaylist: IPlaylist,
        fsMaps: List<FSMap>,
    ): Flow<Result<String>> =
        flow {
            try {
                val playlistPath = (targetPlaylist as FSPlaylistVO).basePath
                fsMaps.forEach {
                    val path = it.playlistBasePath.toPath().resolve(it.dirName)
                    val tarPath = playlistPath.toPath().resolve(it.dirName)
                    path.toFile().renameTo(tarPath.toFile())
                }
                bsHelperDAO.transaction {
                    fsMaps.forEach {
                        bsHelperDAO.fSMapQueries.moveFSMapToPlaylist(
                            targetPlaylist.id,
                            targetPlaylist.basePath,
                            it.mapId,
                            it.playlistId,
                        )
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
            bsHelperDAO.fSPlaylistQueries.deleteAll()
        }
    }

    fun batchInsertBSMap(bsMaps: List<BSMapVO>) {
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
        targetPlaylist: IPlaylist,
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
                        dirName = it.getFilename(),
                        previewStartTime = 0.0,
                        previewDuration = Duration.ZERO,
                        playlistBasePath = targetPlaylist.getTargetPath(),
                        hash = it.versions.firstOrNull()?.version?.hash ?: "",
                        playlistId = targetPlaylist.id,
                        active = false,
                        manageFolderId = targetPlaylist.getMangerFolderId(),
                    ),
                )
            }
        }
    }

    fun activeFSMapByMapId(
        mapId: String,
        playlistId: String,
    ) {
        bsHelperDAO.transaction {
            bsHelperDAO.fSMapQueries.acitveFSMap(mapId, playlistId)
        }
    }

    fun getAllFSMapByPlaylistId(playlistId: String): List<IMap> {
        return bsHelperDAO.fSMapQueries.getAllByPlaylistId(playlistId)
            .executeAsList()
            .mapToVO()
    }

    suspend fun deleteFSMapsByPath(
        playlistId: String,
        fsMaps: List<FSMap>,
    ): Result<String> {
        try {
            logger.trace { "deleteFSMapsByPath: playlistId: $playlistId, path: ${fsMaps.firstOrNull()?.playlistBasePath}, ids:${fsMaps.map { it.mapId }}" }
//            批量删除
            fsMaps.forEach {
                FileSystem.SYSTEM.deleteRecursively(it.playlistBasePath.toPath().resolve(it.dirName), mustExist = true)
            }
            val mapIds = fsMaps.map { it.mapId }
            bsHelperDAO.fSMapQueries.deleteFSMapByMapIdsAndPlaylistId(mapIds, playlistId)
        } catch (e: Exception) {
            return Result.Error(e)
        }
        return Result.Success("")
    }

    fun getLocalMapIdSet(): Flow<Set<Pair<String, String>>> =
        bsHelperDAO.fSMapQueries
            .getAllFSMapId()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map {
                it.map { item -> Pair(item.playlistId, item.mapId) }.toSet()
            }
            .catch {
                emit(setOf())
            }
    fun getLocalMapIdSetByManageFolderId(manageFolderId:Long): Flow<Set<Pair<String, String>>> =
        bsHelperDAO.fSMapQueries
            .getAllFSMapIdByManageFolderId(manageFolderId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map {
                it.map { item -> Pair(item.playlistId, item.mapId) }.toSet()
            }
            .catch {
                emit(setOf())
            }
    fun getLocalMapIdSetByPlaylist(playlistId: String): Flow<Set<Pair<String, String>>> =
        bsHelperDAO.fSMapQueries
            .getAllFSMapId()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map {
                it.map { item -> Pair(item.playlistId, item.mapId) }.toSet()
            }
            .catch {
                emit(setOf())
            }
    suspend fun getBSMapById(id: String):  Result<BSMapVO> {
        bsAPIRepository.getBSMap(id)?.let {
            return Result.Success(it)
        }?:return Result.Error(Exception("not found"))
    }
    suspend fun getBSMapByIds(ids: List<String>): Map<String, BSMapVO> {
        val localMap =
            bsHelperDAO.bSMapQueries.selectAllByMapIds(ids).executeAsList().mapToVO().map {
                it.map.mapId to it
            }.toMap()
        val bsMapVOs =
            ids.filter { !localMap.containsKey(it) }.chunked(50).map {
                return@map bsAPI.getMapsByIds(it).values.map { it.convertToVO() }
            }.flatten()
        batchInsertBSMap(bsMapVOs)
        return bsMapVOs.map { it.map.mapId to it }.toMap() + localMap
    }

    suspend fun getBSMapReviewsById(mapId: String) = bsAPI.getMapReview(mapId)

    fun getPagingBSMapByPlaylistId(playlistId: String) = bsAPIRepository.getPagingBSMapByPlaylistId(playlistId)

    fun getPagingBSMap(mapFilterParam: MapFilterParam) = bsAPIRepository.getPagingBSMap(mapFilterParam)

    fun getPagingBSMapByBSUserId(id: Int) = bsAPIRepository.getPagingBSMapByBSUserId(id)
}
