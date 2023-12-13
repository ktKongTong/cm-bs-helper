package io.ktlab.bshelper.ui.screens.home.bsmap

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.enums.getSortKeyComparator
import io.ktlab.bshelper.ui.components.EmptyContent
import io.ktlab.bshelper.ui.components.MapItem
import io.ktlab.bshelper.ui.components.MapOnlinePreview
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.HomeUIEvent
import io.ktlab.bshelper.viewmodel.MapListState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MapCardList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    mapListState: MapListState,
    mapList: List<IMap>,
    onUIEvent: (UIEvent) -> Unit,
    stickyHeader : @Composable () -> Unit = {},
) {

    val state = rememberLazyListState()
    val mapMultiSelected = mapListState.multiSelectedMapHashMap
    val mapMultiSelectedMode = mapListState.isMapMultiSelectMode
    val sortRule = mapListState.sortRule
//    val context = LocalContext.current
    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        contentPadding = contentPadding,
        state = state,
    ) {
        stickyHeader {
            Surface(Modifier.fillParentMaxWidth()) {
                stickyHeader()
            }
        }
//        mapListFilter
        val mapListFiltered = mapList.filter{true}
        val mapListSorted = mapListFiltered.sortedWith(sortRule.getSortKeyComparator())
        if (mapListSorted.isNotEmpty()) {
            items(mapListSorted.size){
                val map = mapListSorted[it]
                MapItem(
                    map = map,
                    onClick = { onUIEvent(HomeUIEvent.MapTapped(map.getID())) },
                    onLongClick = {},
                    onAvatarClick = {},
                    menuArea = {
                        if (mapMultiSelectedMode) {
                            Checkbox(
                                checked = mapMultiSelected.containsKey(map.getID()),
                                onCheckedChange = { onUIEvent(HomeUIEvent.MapMultiSelected(map)) },
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .align(Alignment.Center)
                            )
                        }else {
                            var previewDialogOpen by remember { mutableStateOf(false) }
                            MapCardMenu(
                                modifier = Modifier
                                    .padding(start = 2.dp)
                                    .align(Alignment.Center),
                                onDelete = {
                                    onUIEvent(HomeUIEvent.MultiDeleteAction(setOf(map)))
                                },
                                onMove = {

                                },
                                onPreview = { previewDialogOpen = true },
                            )
                            if (previewDialogOpen) {
                                MapOnlinePreview(onDismiss = { previewDialogOpen = false }, mapId = map.getID())
                            }
                        }
                    }
                )
            }
        }else {
            item {
                EmptyContent()
            }
        }
    }
}