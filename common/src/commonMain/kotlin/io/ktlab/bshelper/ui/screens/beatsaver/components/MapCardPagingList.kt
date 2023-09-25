package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
//import androidx.paging.LoadState
import io.ktlab.bshelper.model.IMap
//import io.ktlab.bshelper.paging.LazyPagingItems
//import io.ktlab.bshelper.paging.itemKey
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.beatsaver.components.BSMapCard
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent
import io.ktlab.bshelper.viewmodel.LocalState

//import io.ktkt.bshelper.utils.DownloadInfo


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MapCardPagingList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    snackbarHostState: SnackbarHostState,


    localState: LocalState,
    mapMultiSelectedMode: Boolean,
    mapMultiSelected: Set<IMap> = setOf(),
//    mapPagingItems: LazyPagingItems<IMap>,
    downloadingTask: Map<String,DownloadTask>,
    onUIEvent: (UIEvent) -> Unit,
    stickyHeader : @Composable () -> Unit = {},
) {
//    if (mapPagingItems.loadState.refresh is LoadState.Error) {
//        LaunchedEffect(key1 = snackbarHostState) {
//            snackbarHostState.showSnackbar(
//                (mapPagingItems.loadState.refresh as LoadState.Error).error.message ?: ""
//            )
//        }
//    }
//    Box(modifier = modifier.fillMaxSize()) {
//    if (mapPagingItems.loadState.refresh is LoadState.Loading) {
//        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//    }else {
////        Row(
////            modifier = Modifier
////                .fillMaxWidth()
////                .height(48.dp)
////                .padding(horizontal = 16.dp)
////        ) {
////            stickyHeader()
////        }
//        LazyColumn(
//            modifier = modifier,
//            contentPadding = contentPadding,
//            state = state,
//        ) {
//            stickyHeader {
//                Surface(Modifier.fillParentMaxWidth()) {
//                    stickyHeader()
//                }
//            }
//            items(
//                count = mapPagingItems.itemCount,
//                key = mapPagingItems.itemKey { it.getID() },
//            ) { index ->
//                val map = mapPagingItems[index]
//                if (map != null) {
////                    val context = LocalContext.current
//                    BSMapCard(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .combinedClickable(
//                                onLongClick = {
////                                    Toast
////                                        .makeText(context, "MapCardList ${map.getID()} LongClick",
////                                            Toast.LENGTH_SHORT)
////                                        .show()
//                                },
//                                onClick = {
////                                    Toast.makeText(context, "MapCardList ${map.getID()}", Toast.LENGTH_SHORT).show()
////                                    Log.d("MapCardList", "MapCardList ${map.getID()}")
//                                    BeatSaverUIEvent.MapTapped(map)
//                                }),
//                        map = map,
//                        checked = mapMultiSelected.contains(map),
//                        multiSelectedMode = mapMultiSelectedMode,
//                        local = false,
//                        selectableIPlaylists = localState.selectableLocalPlaylists,
//                        downloadInfo = downloadingTask[map.getID()],
//                        onDownloadMap = {it,targetPath,targetPlaylistId-> onUIEvent(BeatSaverUIEvent.DownloadMap(it,targetPath,targetPlaylistId)) },
//                        onPlayPreviewMusicSegment = {
//                            onUIEvent(BeatSaverUIEvent.PlayPreviewMusicSegment(map))
//                        },
//                        onUIEvent = onUIEvent,
//                        onMapMultiSelected = { onUIEvent(BeatSaverUIEvent.MapMultiSelected(it)) },
//
//                    )
//                }
//            }
//            item {
////                if (mapPagingItems.loadState.refresh is LoadState.Loading) {
////                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
////                }
//                if (mapPagingItems.loadState.append is LoadState.Loading) {
//                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
//                }else if(mapPagingItems.loadState.append.endOfPaginationReached){
//                    Row (
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(48.dp),
//                        horizontalArrangement = Arrangement.Center,
//                    ){
//                        Text(text = "😲 no more data", style = MaterialTheme.typography.bodyMedium)
//                    }
//                }
//            }
////            item {
////                EmptyContent(modifier = Modifier.fillMaxSize())
////            }
//        }
//    }
//
//    }

}