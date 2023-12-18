package io.ktlab.bshelper.data.api.paging

import androidx.paging.PagingSource
import app.cash.paging.PagingState
import io.ktlab.bshelper.data.api.BeatSaverAPI
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.dto.response.APIRespResult
import io.ktlab.bshelper.model.dto.response.errorMsg
import io.ktlab.bshelper.model.dto.response.isSuccess
import kotlinx.datetime.LocalDateTime

class BSMapByUserPagingSource(
    private val beatSaverApiService: BeatSaverAPI,
    private val mapperId: Int,
) : PagingSource<LocalDateTime, IMap>() {
    override fun getRefreshKey(state: PagingState<LocalDateTime, IMap>): LocalDateTime? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<LocalDateTime>): LoadResult<LocalDateTime, IMap> {
        return try {
            val before = params.key
            val apiRespResult = beatSaverApiService.getCollaborationMapById(id = mapperId, before = before)
            if (apiRespResult.isSuccess()) {
                val docs = (apiRespResult as APIRespResult.Success).data.docs.map { it.convertToVO() }
                LoadResult.Page(
                    data = docs,
                    prevKey = null,
                    nextKey = if (docs.size < 20) null else docs.lastOrNull()?.map?.uploaded,
                )
            } else {
                LoadResult.Error(apiRespResult.errorMsg())
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
