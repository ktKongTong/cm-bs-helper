package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(loading, onRefresh)
    Box(Modifier.pullRefresh(pullRefreshState)) {
        if (empty) {
            emptyContent()
        } else {
            content()
        }
        PullRefreshIndicator(loading, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun FullScreenLoading() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
    ) {
        CircularProgressIndicator()
    }
}
