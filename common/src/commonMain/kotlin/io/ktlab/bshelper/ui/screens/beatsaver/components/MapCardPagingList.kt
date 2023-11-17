package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
//import app.cash.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import androidx.paging.LoadState
import app.cash.paging.compose.itemKey
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.repository.IDownloadTask
//import io.ktlab.bshelper.paging.LazyPagingItems
//import io.ktlab.bshelper.paging.itemKey
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.beatsaver.components.BSMapCard
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
                    contentPadding = contentPadding,
                ) {

                    items(
                        count = mapPagingItems.itemCount,
                        key = mapPagingItems.itemKey { it.getID() },
                        span = { GridItemSpan(1) }
                    ) { index ->
                        val map = mapPagingItems[index]
                        if (map != null) {
                            BSMapCard(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .combinedClickable(
                                        onLongClick = {},
                                        onClick = {
                                            BeatSaverUIEvent.MapTapped(map)
                                        }),
                                map = map,
                                checked = mapMultiSelected.contains(map),
                                multiSelectedMode = mapMultiSelectedMode,
                                local = false,
                                selectableIPlaylists = localState.selectableLocalPlaylists,
                                downloadInfo = downloadingTask[map.getID()],
                                onDownloadMap = {targetMap-> onUIEvent(BeatSaverUIEvent.DownloadMap(targetMap)) },
                                onPlayPreviewMusicSegment = {
                                    onUIEvent(BeatSaverUIEvent.PlayPreviewMusicSegment(map))
                                },
                                onUIEvent = onUIEvent,
                                onMapMultiSelected = { onUIEvent(BeatSaverUIEvent.MapMultiSelected(it)) },

                            )
                        }
                    }
                    item {
                        if (mapPagingItems.loadState.append is LoadState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }else if(mapPagingItems.loadState.append.endOfPaginationReached){
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                horizontalArrangement = Arrangement.Center,
                            ){
                                Text(text = "😲 no more data", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }

    }

}