package io.ktlab.bshelper.ui.screens.beatsaver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.beatsaver.components.BSMapDetail
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
    modifier: Modifier = Modifier
){
    uiState as BeatSaverUiState.PlaylistQuery
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .widthIn(max = 300.dp)
            .fillMaxWidth()
    ) {
        PlaylistFilterPanel(
            playlistFilterPanelState = uiState.playlistFilterPanelState,
            onUIEvent = onUIEvent
        )
    }
    val playlistPagingItems = uiState.playlistFlow.collectAsLazyPagingItems()
    if (uiState.selectedBSMap != null) {
        BSMapDetail(
            map = uiState.selectedBSMap,
            onUIEvent = onUIEvent,
            comments = uiState.selectedBSMapReview,
        )
    }
    if (uiState.selectedBSPlaylist != null) {
        BSPlaylistDetail(
            playlist = uiState.selectedBSPlaylist,
            onUIEvent = onUIEvent,
            localState = uiState.localState,
            mapFlow = uiState.mapFlow,
            snackbarHostState = snackbarHostState,
            uiState = uiState,

        )
    }else {
        PlaylistPagingList(
            Modifier,
            snackbarHostState = snackbarHostState,
            playlistPagingItems = playlistPagingItems,
            localState = uiState.localState,
            mapMultiSelectedMode = uiState.multiSelectMode,
            mapMultiSelected = uiState.multiSelectedBSMap,
            onUIEvent = onUIEvent,
            stickyHeader = {
                Row (
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                ){
                    Text(
                        text = "Playlists",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            },
            downloadingTask = emptyMap(),
        )
    }

}