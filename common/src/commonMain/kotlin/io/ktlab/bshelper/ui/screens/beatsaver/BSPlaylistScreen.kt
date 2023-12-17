package io.ktlab.bshelper.ui.screens.beatsaver

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.beatsaver.components.BSPlaylistDetail
import io.ktlab.bshelper.ui.screens.beatsaver.components.PlaylistFilterPanel
import io.ktlab.bshelper.ui.screens.beatsaver.components.PlaylistPagingList
import io.ktlab.bshelper.viewmodel.BeatSaverUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BSPlaylistScreen(
    uiState: BeatSaverUiState,
    snackbarHostState: SnackbarHostState,
    onUIEvent: (UIEvent) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
//    uiState as BeatSaverUiState.PlaylistQuery
    Row {
        Box(
            modifier =
                modifier
                    .clip(shape = RoundedCornerShape(10.dp))
                    .widthIn(max = 300.dp)
                    .fillMaxWidth(),
        ) {
            PlaylistFilterPanel(uiState.playlistFilterPanelState, onUIEvent)
        }
        val playlistPagingItems = uiState.playlistFlow.collectAsLazyPagingItems()
        AnimatedContent(
            targetState = uiState.selectedBSPlaylist,
            transitionSpec = {
                (
                    fadeIn() +
                        slideInVertically(
                            animationSpec = tween(400),
                            initialOffsetY = {
                                if (targetState == null) {
                                    -it
                                } else {
                                    it
                                }
                            },
                        )
                )
                    .togetherWith(
                        fadeOut(animationSpec = tween()) +
                            slideOutVertically(
                                animationSpec = tween(400),
                                targetOffsetY = {
                                    if (targetState == null) {
                                        it
                                    } else {
                                        -it
                                    }
                                },
                            ),
                    )
            },
        ) {
            if (it != null) {
                BSPlaylistDetail(
                    playlist = it,
                    onUIEvent = onUIEvent,
                    localState = uiState.localState,
                    mapFlow = uiState.mapFlow,
                    snackbarHostState = snackbarHostState,
                    uiState = uiState,
                )
            } else {
                val downloadingTasks =
                    uiState.downloadTaskFlow.collectAsState(initial = emptyList()).value
                        .filter { it is IDownloadTask.PlaylistDownloadTask }
                        .map { it as IDownloadTask.PlaylistDownloadTask }
                        .associateBy { it.playlist.id }
                PlaylistPagingList(
                    Modifier,
                    snackbarHostState = snackbarHostState,
                    playlistPagingItems = playlistPagingItems,
                    localState = uiState.localState,
                    lazyListState = lazyListState,
                    mapMultiSelectedMode = uiState.multiSelectMode,
                    multiSelectedMaps = uiState.multiSelectedBSMap,
                    onUIEvent = onUIEvent,
                    stickyHeader = {
                        Row(
                            modifier =
                                Modifier
                                    .padding(horizontal = 20.dp)
                                    .fillMaxWidth(),
                        ) {
                            Text(
                                text = "Playlists",
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                    },
                    downloadingTask = downloadingTasks,
                )
            }
        }
    }
}
