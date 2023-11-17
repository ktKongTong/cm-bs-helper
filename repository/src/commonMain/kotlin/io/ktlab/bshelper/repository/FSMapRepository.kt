package io.ktlab.bshelper.repository

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.ktlab.bshelper.api.BeatSaverAPI
import io.ktlab.bshelper.model.*
import io.ktlab.bshelper.model.dto.request.MapFilterParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import io.ktlab.bshelper.model.mapper.mapToVO
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.model.vo.FSPlaylistVO
import io.ktlab.bshelper.paging.BSMapPagingSource
import io.ktlab.bshelper.paging.BSPlaylistDetailPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.io.path.Path
import kotlin.io.path.pathString

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
                val path = Path(it.playlistBasePath,it.dirFilename)
                val tarPath = Path(playlistPath,it.dirFilename)
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

//    suspend fun getBSMapById(mapId:String):BSMapView {
//        fsMapDao.geBSMapById(mapId).let {
//            if (it == null) {
//                // 获取 bsMap 的详情
//                val bsMap = beatSaverAPIService.getMapById(mapId)
//                // 写入 uploader
//                bsMap.uploader.convertToEntity().let { uploader ->
//                    fsMapDao.insertBSUser(listOf(uploader))
//                }
//                // 写入 diff
//                bsMap.versions.first().let { versionDto ->
//                    val version = versionDto.convertToEntity(mapId)
//                    val diffs = versionDto.diffs.map { diffDto ->
//                        diffDto.convertToEntity(hash = version.hash, mapId = bsMap.id)
//                    }
//                    fsMapDao.insertVersion(version)
//                    fsMapDao.insertDiffAll(diffs)
//                }
//                // 写入 map
//                fsMapDao.insertBSAll(listOf(bsMap.convertToBSEntity()))
//                return fsMapDao.geBSMapById(mapId)!!
//            }
//            return it
//        }
//    }

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
                FileSystem.SYSTEM.deleteRecursively(it.playlistBasePath.toPath().resolve(it.dirFilename))
            }
            val mapIds = fsMaps.map { it.mapId }
            bsHelperDAO.fSMapQueries.deleteFSMapByMapIdsAndPlaylistId(mapIds,playlistId)
        } catch (e: Exception) {
            return Result.Error(e)
        }
        return Result.Success("")
    }
//    private suspend fun fsMapProcess(it: FSMapView, playlistId:String? = null):FSMapView {
//        try {
//            if (it.bsMapWithUploader != null) {
//                return it
//            }
//            // 获取 bsMap 的详情
//            val bsMap = beatSaverAPIService.getMapById(it.fsMap.mapId)
//            // 写入 uploader
//            bsMap.uploader.convertToEntity().let { uploader ->
//                fsMapDao.insertBSUser(listOf(uploader))
//            }
//            // 写入 diff
//            bsMap.versions.first().let { versionDto ->
//                val version = versionDto.convertToEntity(it.fsMap.mapId)
//                val diffs = versionDto.diffs.map { diffDto ->
//                    diffDto.convertToEntity(hash = version.hash, mapId = bsMap.id)
//                }
//                fsMapDao.insertVersion(version)
//                fsMapDao.insertDiffAll(diffs)
//            }
//            // 写入 map
//            fsMapDao.insertBSAll(listOf(bsMap.convertToBSEntity()))
//            return it.copy(
//                bsMapWithUploader = bsMap.convertToBSWithUploader()
//            )
//        } catch (e: Exception) {
//            Log.e("MapRepository", e.toString())
//            return it
//        }
//    }

//    override suspend fun getMapByMapId(mapId: String): Flow<Result<IMap>> = flow {
//        try {
//            val map = fsMapProcess(fsMapDao.getMapByMapId(mapId))
//            emit(Result.Success(map))
//        } catch (e: Exception) {
//            Log.e("MapRepository", e.toString())
//        }
//    }

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


}