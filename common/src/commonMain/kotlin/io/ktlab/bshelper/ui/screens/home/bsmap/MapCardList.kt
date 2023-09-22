package io.ktlab.bshelper.ui.screens.home.bsmap

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.successOr
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.enums.getSortKeyComparator
import io.ktlab.bshelper.ui.components.EmptyContent
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
                MapCard(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
//                            Toast
//                                .makeText(context, "MapCardList ${map.getID()}", Toast.LENGTH_SHORT)
//                                .show()
                            onUIEvent(HomeUIEvent.MapTapped(map.getID()))
                        },
                    map = map,
                    checked = mapMultiSelected.containsKey(map.getID()),
                    multiSelectedMode = mapMultiSelectedMode,
                    onUIEvent = onUIEvent,
                )
            }
        }else {
            item {
                EmptyContent()
            }
        }
    }
}