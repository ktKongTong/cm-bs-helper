package io.ktlab.bshelper.ui.screens.home.bsmap

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.enums.getSortKeyComparator
import io.ktlab.bshelper.ui.LocalUIEventHandler
import io.ktlab.bshelper.ui.components.EmptyContent
import io.ktlab.bshelper.ui.viewmodel.MapListState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MapCardList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    mapListState: MapListState,
    mapList: List<IMap>,
    stickyHeader: @Composable () -> Unit = {},
) {
    val onUIEvent = LocalUIEventHandler.current
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
        val mapListFiltered = mapList.filter { true }
        val mapListSorted = mapListFiltered.sortedWith(sortRule.getSortKeyComparator())
        if (mapListSorted.isNotEmpty()) {
            items(mapListSorted.size) {
                val map = mapListSorted[it]
                MapCard(
                    map = map,
                    checked = mapMultiSelected.containsKey(map.getID()),
                    multiSelectedMode = mapMultiSelectedMode,
                    onUIEvent = onUIEvent,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        } else {
            item {
                EmptyContent(Modifier.padding(top = 32.dp))
            }
        }
    }
}
