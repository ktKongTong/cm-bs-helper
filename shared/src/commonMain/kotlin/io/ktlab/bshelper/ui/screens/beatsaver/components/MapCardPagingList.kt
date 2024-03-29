package io.ktlab.bshelper.ui.screens.beatsaver.components

// import app.cash.paging.LoadState
// import io.ktlab.bshelper.paging.LazyPagingItems
// import io.ktlab.bshelper.paging.itemKey
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.ui.event.BeatSaverUIEvent
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.viewmodel.LocalState

// import io.ktkt.bshelper.utils.DownloadInfo

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MapCardPagingList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyGridState = rememberLazyGridState(),
    snackbarHostState: SnackbarHostState,
    localState: LocalState,
    mapMultiSelectedMode: Boolean,
    mapMultiSelected: Set<IMap> = setOf(),
    selectedBSMap: IMap? = null,
    mapPagingItems: LazyPagingItems<IMap>,
    downloadingTask: Map<String, IDownloadTask.MapDownloadTask>,
    onUIEvent: (UIEvent) -> Unit,
    stickyHeader: @Composable () -> Unit = {},
) {
    if (mapPagingItems.loadState.refresh is LoadState.Error) {
        LaunchedEffect(key1 = snackbarHostState) {
            snackbarHostState.showSnackbar(
                (mapPagingItems.loadState.refresh as LoadState.Error).error.message ?: "",
            )
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        val windowSizeClass = calculateWindowSizeClass().widthSizeClass
        val size =
            when (windowSizeClass) {
                WindowWidthSizeClass.Expanded -> 2
                else -> 1
            }
        Column {
            stickyHeader()
            if (mapPagingItems.loadState.refresh is LoadState.Loading) {
                LoadingPlaceholder()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(size),
                    contentPadding = contentPadding,
                    state = state,
                ) {
                    // https://issuetracker.google.com/issues/259686541
                    items(
                        count = mapPagingItems.itemCount,
                        key = mapPagingItems.itemKey { (it as BSMapVO).versions.first().version.hash },
                        span = { GridItemSpan(1) },
                    ) { index ->
                        val map = mapPagingItems[index]
//                    require(map != null){ "map should not be null" }
                        if (map != null) {
                            val selectablePlaylists = localState.selectableLocalPlaylistFlow.collectAsState(emptyList())
                            BSMapCard(
                                modifier = Modifier.fillMaxSize(),
                                selectedBSMap = selectedBSMap,
                                map = map,
                                checked = mapMultiSelected.contains(map),
                                multiSelectedMode = mapMultiSelectedMode,
                                localInfo = localState.localMapIdMap[map.getID()],
                                selectableIPlaylists = selectablePlaylists.value,
                                downloadInfo = downloadingTask[map.getID() + localState.targetPlaylist?.id],
                                onDownloadMap = { targetMap -> onUIEvent(BeatSaverUIEvent.DownloadMap(targetMap)) },
                                onPlayPreviewMusicSegment = {
                                    onUIEvent(BeatSaverUIEvent.PlayPreviewMusicSegment(map))
                                },
                                onUIEvent = onUIEvent,
                                onMapMultiSelected = { onUIEvent(BeatSaverUIEvent.OnMultiSelectMap(it)) },
                            )
                        }
                    }
                    item(key = "loading state", { GridItemSpan(maxCurrentLineSpan) }) {
                        if (mapPagingItems.loadState.append is LoadState.Loading || mapPagingItems.loadState.refresh is LoadState.Loading) {
                            LoadingPlaceholder()
                        } else if (mapPagingItems.loadState.append.endOfPaginationReached) {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxSize().height(48.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(text = "😲 no more data", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingPlaceholder() {
    Box(
        modifier =
            Modifier
                .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp).size(48.dp))
    }
}
