package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.repository.IDownloadTask
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent
import io.ktlab.bshelper.viewmodel.LocalState


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlaylistPagingList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyGridState = rememberLazyGridState(),
    snackbarHostState: SnackbarHostState,
    localState: LocalState,
    mapMultiSelectedMode: Boolean,
    mapMultiSelected: Set<IMap> = setOf(),
    playlistPagingItems: LazyPagingItems<IPlaylist>,
    downloadingTask: Map<String, IDownloadTask.MapDownloadTask>,
    onUIEvent: (UIEvent) -> Unit,
    stickyHeader : @Composable () -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    if (playlistPagingItems.loadState.refresh is LoadState.Error) {
        LaunchedEffect(key1 = snackbarHostState) {
            snackbarHostState.showSnackbar(
                (playlistPagingItems.loadState.refresh as LoadState.Error).error.message ?: ""
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        val windowSizeClass = calculateWindowSizeClass().widthSizeClass
        val size = when (windowSizeClass) {
            WindowWidthSizeClass.Expanded -> 2
            else -> 1
        }
        Column {
            stickyHeader()
//            Divider(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(1.dp)
//                    .padding(horizontal = 16.dp),
//                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
//            )
            if (playlistPagingItems.loadState.refresh is LoadState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }else {
                LazyColumn (
//                    columns = GridCells.Fixed(size),
                    contentPadding = contentPadding,
                    state = lazyListState,
                ) {
                    items(
                        count = playlistPagingItems.itemCount,
                        key = playlistPagingItems.itemKey { it.id + it.title },
                    ) { index ->
                        val playlist = playlistPagingItems[index]
                        playlist?.let {
                            BSPlaylistCard(
                                playlist = it,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                onUIEvent = onUIEvent,
                            )
                        }
                    }
                    item {
                        if (playlistPagingItems.loadState.append is LoadState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }else if(playlistPagingItems.loadState.append.endOfPaginationReached){
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