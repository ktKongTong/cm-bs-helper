package io.ktlab.bshelper.repository

//import androidx.paging.Pager
//import androidx.paging.PagingConfig
//import androidx.paging.PagingData
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
import io.ktlab.bshelper.model.vo.FSPlaylistVO
import io.ktlab.bshelper.paging.BSMapPagingSource
//import io.ktlab.bshelper.paging.BSMapPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
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

    suspend fun deleteAll() {
        bsHelperDAO.transaction {
//            bsHelperDAO.fSMapQueries.deleteAll()
//            bsHelperDAO.bSMapQueries.deleteAll()
//            bsHelperDAO.bSMapVersionQueries.deleteAll()
//            bsHelperDAO.bSUserQueries.deleteAll()
//            bsHelperDAO.fSPlaylistQueries.deleteAll()
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
//        bsMap: BSMapView
    ) {
//        fsMapDao.insertBSAll(listOf(bsMap.map))
//        fsMapDao.insertBSUser(listOf(bsMap.uploader))
//        fsMapDao.insertVersion(bsMap.versions.first().version)
//        fsMapDao.insertDiffAll(bsMap.versions.first().diffs)
    }

    suspend fun batchInsertBSMap(
//        bsMaps: List<BSMapView>
    ) {
//        fsMapDao.insertBSAll(bsMaps.map { it.map })
//        fsMapDao.insertBSUser(bsMaps.map { it.uploader })
//        fsMapDao.insertVersionAll(bsMaps.map { it.versions.first().version })
//        fsMapDao.insertDiffAll(bsMaps.flatMap { it.versions.first().diffs })
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
                val path = Path(it.playlistBasePath,it.dirFilename)
                val res = path.toFile().deleteRecursively()
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
}