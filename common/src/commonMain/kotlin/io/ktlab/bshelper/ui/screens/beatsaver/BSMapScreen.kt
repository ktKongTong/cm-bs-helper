package io.ktlab.bshelper.ui.screens.beatsaver

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import io.ktlab.bshelper.repository.IDownloadTask
import io.ktlab.bshelper.ui.screens.beatsaver.components.BSMapCardListHeader
import io.ktlab.bshelper.ui.screens.beatsaver.components.BSMapDetail
import io.ktlab.bshelper.ui.screens.beatsaver.components.MapCardPagingList
import io.ktlab.bshelper.ui.screens.beatsaver.components.MapFilterPanel
import io.ktlab.bshelper.viewmodel.BeatSaverUiState

@Composable
fun BSMapScreen(
    uiState: BeatSaverUiState,
    snackbarHostState: SnackbarHostState,
    onUIEvent: (io.ktlab.bshelper.ui.event.UIEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    uiState as BeatSaverUiState.MapQuery
    Box(
        modifier = modifier
            .widthIn(Dp.Unspecified,300.dp)
            .fillMaxWidth()
    ){
        MapFilterPanel(
            mapFilterPanelState = uiState.mapFilterPanelState,
            onUIEvent = onUIEvent,
        )
    }
    val downloadingTasks= uiState.downloadTaskFlow.collectAsState(initial = emptyList()).value.flatMap {
        when(it) {
            is IDownloadTask.MapDownloadTask -> listOf(it)
            is IDownloadTask.BatchDownloadTask -> it.taskList
            is IDownloadTask.PlaylistDownloadTask -> it.taskList
        }
    }.associateBy { it.downloadTaskModel.relateEntityId!! }
    val mapPagingItems = uiState.mapFlow.collectAsLazyPagingItems()

    Box(
        modifier = modifier
        .fillMaxWidth()
    ) {

            if (uiState.selectedBSMap != null) {
                BSMapDetail(
                    map = uiState.selectedBSMap,
                    comments = uiState.selectedBSMapReview,
                    onUIEvent = onUIEvent)
            } else {
                MapCardPagingList(
                    Modifier,
                    snackbarHostState = snackbarHostState,
                    mapPagingItems = mapPagingItems,
                    localState = uiState.localState,
                    mapMultiSelectedMode = uiState.multiSelectMode,
                    mapMultiSelected = uiState.multiSelectedBSMap,
                    selectedBSMap = uiState.selectedBSMap,
                    onUIEvent = onUIEvent,
                    stickyHeader = {
                        BSMapCardListHeader(
                            count = 0,
                            localState = uiState.localState,
                            multiSelectedMode = uiState.multiSelectMode,
                            multiSelectedBSMap = uiState.multiSelectedBSMap,
                            onUIEvent = onUIEvent,
                        )
                    },
                    downloadingTask = downloadingTasks,
                )
            }
        }


}