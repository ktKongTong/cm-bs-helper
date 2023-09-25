package io.ktlab.bshelper.ui.screens.beatsaver

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.ui.screens.beatsaver.components.BSMapCardListHeader
import io.ktlab.bshelper.ui.screens.beatsaver.components.MapCardPagingList
import io.ktlab.bshelper.ui.screens.beatsaver.components.MapFilterPanel
import io.ktlab.bshelper.rememberContentPaddingForScreen
import io.ktlab.bshelper.ui.components.BSHelperSnackbarHost
import io.ktlab.bshelper.ui.components.Developing
import io.ktlab.bshelper.ui.components.SnackBarShown
import io.ktlab.bshelper.ui.components.VerticalDivider
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverUiState

//import io.ktlab.bshelper.paging.collectAsLazyPagingItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeatSaverScreen(
    uiState: BeatSaverUiState,
    showTopAppBar: Boolean,
    snackbarHostState: SnackbarHostState,
    openDrawer: () -> Unit,
    onUIEvent: (UIEvent) -> Unit,
    onSnackBarShown: (Long) -> Unit,
    modifier: Modifier = Modifier
){
    val contentPadding = rememberContentPaddingForScreen(
        additionalTop = if (showTopAppBar) 0.dp else 8.dp,
        excludeTop = showTopAppBar
    )
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    Scaffold(
        snackbarHost = { BSHelperSnackbarHost(hostState = snackbarHostState) },
        topBar = {
//            if (showTopAppBar) {
//                BeatSaverTopAppBar(
//                    openDrawer = {  },
//                    topAppBarState = topAppBarState
//                )
//            }
        },
        modifier = modifier
    ) { innerPadding ->
        val contentModifier = Modifier
            .padding(innerPadding)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
        val contentPadding = rememberContentPaddingForScreen(
            additionalTop = if (showTopAppBar) 0.dp else 8.dp,
            excludeTop = showTopAppBar
        )
        var tabState by remember { mutableStateOf(0) }
        Row(contentModifier) {
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                TextTabs(
                    selectedIndex = tabState,
                    onClickTab = { tabState = it }
                )
                when(tabState) {
                    0 -> {
                        MapFilterPanel(
//                            modifier = Modifier.weight(1f),
                            mapFilterPanelState = (uiState as BeatSaverUiState.MapQuery).mapFilterPanelState,
                            onUIEvent = onUIEvent,
                        )
                    }
                    1 -> {
                        Developing()
                    }
                    2 -> {
                        Developing()
                    }
                }
            }
            VerticalDivider()
            uiState as BeatSaverUiState.MapQuery
//            val downloadingTasks= uiState.downloadTaskFlow.collectAsState(initial = emptyList()).value.filter {
////                it is DownloadTask.MapDownloadTask
//                it.taskType == DownloadType.MAP && it.status !=DownloadStatus.FINISHED && it.relateMap != null
//            }.associateBy { it.relateMap!!.getID() }
//            val mapPagingItems = uiState.mapFlow.collectAsLazyPagingItems()
            MapCardPagingList(
                Modifier.weight(2f,false),
                snackbarHostState = snackbarHostState,
//                mapPagingItems = mapPagingItems,
                localState = uiState.localState,
                mapMultiSelectedMode = uiState.multiSelectMode,
                mapMultiSelected = uiState.multiSelectedBSMap,
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
                downloadingTask = mapOf(),
            )
        }

    }
    SnackBarShown(
        snackbarHostState = snackbarHostState,
        snackBarMessages = uiState.snackBarMessages,
        onSnackBarShown = onSnackBarShown,
    )
}

@Composable
fun TextTabs(
    selectedIndex : Int,
    onClickTab: (Int) -> Unit
) {
    val titles = listOf("Default", "Playlist", "Mapper")
    Column {
        TabRow(selectedTabIndex = selectedIndex) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { onClickTab(index) },
                    text = { Text(text = title, overflow = TextOverflow.Ellipsis) }
                )
            }
        }
    }
}