package io.ktlab.bshelper.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.ktlab.bshelper.model.BSHelperDatabase
import io.ktlab.bshelper.model.IMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.mapper.FSMapVO
import io.ktlab.bshelper.model.mapper.getAllByPlaylistIdQueryMapper
import kotlinx.coroutines.Dispatchers

class FSMapRepository(
    private val bsHelperDAO:BSHelperDatabase,
) {
    fun getFlowMapsByPlaylistId(playlistId: String): Flow<Result<List<IMap>>> = flow {
        try {
            bsHelperDAO.fSMapQueries.getAllByPlaylistId(playlistId)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .collect{ q->
//                    val grouped = q
//                        .groupBy { it.mapId }
//                        .entries.map
//                val d = data
                emit(Result.Success(listOf()))
//                val src = data.toMutableList()
//                src.forEachIndexed{i,it->
//                    if(it.bsMapWithUploader == null){
//                        src[i] = fsMapProcess(it)
//                    }
//                    emit(Result.Success(src))
//                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}