package io.ktlab.bshelper.data.api.paging

import androidx.paging.PagingSource
import app.cash.paging.PagingState
import io.ktlab.bshelper.data.api.BeatSaverAPI
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.dto.request.PlaylistFilterParam
import io.ktlab.bshelper.model.dto.response.APIRespResult
import io.ktlab.bshelper.model.dto.response.errorMsg
import io.ktlab.bshelper.model.dto.response.isSuccess

class BSPlaylistPagingSource(
    private val beatSaverApiService: BeatSaverAPI,
    private val playlistFilterParam: PlaylistFilterParam,
) : PagingSource<Int, IPlaylist>() {
    override fun getRefreshKey(state: PagingState<Int, IPlaylist>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IPlaylist> {
        return try {
            val page = params.key ?: 0
            val apiRespResult =
                beatSaverApiService.searchPlaylist(
                    page = page,
                    queryParam = playlistFilterParam,
                )
            if (apiRespResult.isSuccess()) {
                val docs = (apiRespResult as APIRespResult.Success).data.docs
                LoadResult.Page(
                    data = docs.map { it.convertToVO() },
                    prevKey = null,
                    nextKey = if (docs.size < 20) null else page + 1,
                )
            } else {
                LoadResult.Error(apiRespResult.errorMsg())
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
