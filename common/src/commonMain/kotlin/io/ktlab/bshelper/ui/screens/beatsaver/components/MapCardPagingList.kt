package io.ktlab.bshelper.ui.screens.beatsaver.components

//import app.cash.paging.LoadState
//import io.ktlab.bshelper.paging.LazyPagingItems
//import io.ktlab.bshelper.paging.itemKey
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent
import io.ktlab.bshelper.viewmodel.LocalState

//import io.ktkt.bshelper.utils.DownloadInfo


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
    stickyHeader : @Composable () -> Unit = {},
) {
    if (mapPagingItems.loadState.refresh is LoadState.Error) {
        LaunchedEffect(key1 = snackbarHostState) {
            snackbarHostState.showSnackbar(
                (mapPagingItems.loadState.refresh as LoadState.Error).error.message ?: ""
            )
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        val windowSizeClass = calculateWindowSizeClass().widthSizeClass
        val size = when(windowSizeClass) {
            WindowWidthSizeClass.Expanded -> 2
            else -> 1
        }
        Column {
            stickyHeader()
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
            if (mapPagingItems.loadState.refresh is LoadState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(size),
                    // remember scroll state to scroll to the position when items count changes
                    contentPadding = contentPadding,
                    state = state,
                ) {

                items(
                    count = mapPagingItems.itemCount,
                    key = mapPagingItems.itemKey { it.getID() },
                    span = { GridItemSpan(1) }
                ) { index ->
                    val map = mapPagingItems[index]
                    if (map != null) {
                        val local =
                            localState.targetPlaylist != null && localState.localMapIdSet.contains(localState.targetPlaylist.id to map.getID())
                        BSMapCard(
                            modifier = Modifier.fillMaxSize(),
                            map = map,
                            checked = mapMultiSelected.contains(map),
                            multiSelectedMode = mapMultiSelectedMode,
                            local = local,
                            selectableIPlaylists = localState.selectableLocalPlaylists,
                            downloadInfo = downloadingTask[map.getID()],
                            onDownloadMap = { targetMap -> onUIEvent(BeatSaverUIEvent.DownloadMap(targetMap)) },
                            onPlayPreviewMusicSegment = {
                                onUIEvent(BeatSaverUIEvent.PlayPreviewMusicSegment(map))
                            },
                            onUIEvent = onUIEvent,
                            onMapMultiSelected = { onUIEvent(BeatSaverUIEvent.OnMultiSelectMap(it)) },
                        )
                    }
                }
                item {
                    if (mapPagingItems.loadState.append is LoadState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    } else if (mapPagingItems.loadState.append.endOfPaginationReached) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(text = "😲 no more data", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            }
        }
    }
}