package io.ktlab.bshelper.data.repository

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import io.ktlab.bshelper.data.api.BeatSaverAPI
import io.ktlab.bshelper.data.api.paging.BSMapByUserPagingSource
import io.ktlab.bshelper.data.api.paging.BSMapPagingSource
import io.ktlab.bshelper.data.api.paging.BSMapperPagingSource
import io.ktlab.bshelper.data.api.paging.BSPlaylistDetailPagingSource
import io.ktlab.bshelper.data.api.paging.BSPlaylistPagingSource
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.dto.BSUserWithStatsDTO
import io.ktlab.bshelper.model.dto.request.MapFilterParam
import io.ktlab.bshelper.model.dto.request.PlaylistFilterParam
import io.ktlab.bshelper.model.dto.response.APIRespResult
import io.ktlab.bshelper.model.dto.response.BSMapReviewDTO
import io.ktlab.bshelper.model.dto.response.BSMapperDetailDTO
import kotlinx.coroutines.flow.Flow

class BSAPIRepository(
    private val bsAPI: BeatSaverAPI,
) {
    fun getPagingBSMapByPlaylistId(playlistId: String): Flow<PagingData<IMap>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                BSPlaylistDetailPagingSource(bsAPI, playlistId)
            },
        ).flow
    }

    fun getPagingBSMap(mapFilterParam: MapFilterParam): Flow<PagingData<IMap>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                BSMapPagingSource(bsAPI, mapFilterParam)
            },
        ).flow
    }

    fun getPagingBSMapByBSUserId(id: Int): Flow<PagingData<IMap>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { BSMapByUserPagingSource(bsAPI, id) },
        ).flow
    }

    suspend fun getBSUserDetail(id: Int): Result<BSMapperDetailDTO> {
        when (val res = bsAPI.getMapperDetail(id)) {
            is APIRespResult.Success -> {
                return Result.Success(res.data)
            }
            is APIRespResult.Error -> {
                return Result.Error(res.exception)
            }
        }
    }



    fun getPlaylistDetailPagingMaps(playlistId: String): Flow<PagingData<IMap>> {
        return Pager(
            config = PagingConfig(pageSize = 100, enablePlaceholders = false),
            pagingSourceFactory = {
                BSPlaylistDetailPagingSource(bsAPI, playlistId)
            },
        ).flow
    }

    // most less than 100, todo: need improve
    suspend fun getPlaylistDetailAllMaps(playlistId: String): List<IMap> {
        var page = 0
        val maps =
            bsAPI.getPlaylistDetail(playlistId, page).let { resp ->
                resp as APIRespResult.Success
                resp.data.maps.map { it.map.convertToVO() }
            }
        while (maps.size == 100) {
            page++
            val newMaps =
                bsAPI.getPlaylistDetail(playlistId, page).let { resp ->
                    resp as APIRespResult.Success
                    resp.data.maps.map { it.map.convertToVO() }
                }
            maps.plus(newMaps)
        }
        return maps
    }

    fun getPagingBSPlaylist(playlistFilterParam: PlaylistFilterParam): Flow<PagingData<IPlaylist>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                BSPlaylistPagingSource(bsAPI, playlistFilterParam)
            },
        ).flow
    }

    fun getPagingBSUser(): Flow<PagingData<BSUserWithStatsDTO>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                BSMapperPagingSource(bsAPI)
            },
        ).flow
    }

    suspend fun getBSMapReviewsById(mapId: String): APIRespResult<List<BSMapReviewDTO>> {
        return bsAPI.getMapReview(mapId)
    }
}