package io.ktlab.bshelper.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.ktlab.bshelper.api.BeatSaverAPI
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.dto.request.MapFilterParam
import io.ktlab.bshelper.model.dto.response.APIRespResult
import io.ktlab.bshelper.model.dto.response.errorMsg
import io.ktlab.bshelper.model.dto.response.isSuccess

//
class BSMapPagingSource(
    private val beatSaverApiService: BeatSaverAPI,
    private val mapFilterParam: MapFilterParam,
): PagingSource<Int, IMap>() {
    override fun getRefreshKey(state: PagingState<Int, IMap>): Int? {

        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IMap> {
        return try {
            val page = params.key ?: 0
//            Log.d("BSMapPagingSource", "load: ${mapFilterParam.queryKey} page:$page")
            val apiRespResult = beatSaverApiService.searchMap(
                page = page,
                queryParam = mapFilterParam
            )
            if (apiRespResult.isSuccess()){
                val docs = (apiRespResult as APIRespResult.Success).data.docs.map { it.convertToVO() }
                LoadResult.Page(
                    data = docs,
                    prevKey = null,
                    nextKey= if (docs.size < 20) null else page + 1
                )
            }else {
                LoadResult.Error(apiRespResult.errorMsg())
            }

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}