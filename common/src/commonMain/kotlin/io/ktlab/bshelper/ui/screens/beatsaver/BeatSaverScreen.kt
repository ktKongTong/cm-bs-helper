package io.ktlab.bshelper.ui.screens.beatsaver

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import io.ktlab.bshelper.rememberContentPaddingForScreen
import io.ktlab.bshelper.repository.IDownloadTask
import io.ktlab.bshelper.ui.components.BSHelperSnackbarHost
import io.ktlab.bshelper.ui.components.Developing
import io.ktlab.bshelper.ui.components.SnackBarShown
import io.ktlab.bshelper.ui.components.VerticalDivider
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.beatsaver.components.*
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverUiState
import io.ktlab.bshelper.viewmodel.TabType
import kotlinx.coroutines.flow.toCollection

//import io.ktlab.bshelper.paging.collectAsLazyPagingItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeatSaverScreen(
    uiState: BeatSaverUiState,
    showTopAppBar: Boolean,
    snackbarHostState: SnackbarHostState,
    openDrawer: () -> Unit,
    onUIEvent: (UIEvent) -> Unit,
//    onSnackBarShown: (Long) -> Unit,
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
        Row(
            contentModifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .widthIn(Dp.Unspecified,300.dp)
                    .fillMaxWidth()
            ) {
                TextTabs(
                    selectedTab = uiState.tabType,
                    onClickTab = { onUIEvent(BeatSaverUIEvent.SwitchTab(it)) }
                )
                when(uiState.tabType) {
                    TabType.Map -> {
                        MapFilterPanel(
                            mapFilterPanelState = (uiState as BeatSaverUiState.MapQuery).mapFilterPanelState,
                            onUIEvent = onUIEvent,
                        )
                    }
                    TabType.Playlist -> {
                        PlaylistFilterPanel(
                            playlistFilterPanelState = (uiState as BeatSaverUiState.PlaylistQuery).playlistFilterPanelState,
                            onUIEvent = onUIEvent
                        )
                    }
                }
            }
            VerticalDivider()
            when(uiState.tabType) {
                TabType.Map -> {
                    uiState as BeatSaverUiState.MapQuery
                    val downloadingTasks= uiState.downloadTaskFlow.collectAsState(initial = emptyList()).value.flatMap {
                        when(it) {
                            is IDownloadTask.MapDownloadTask -> listOf(it)
                            is IDownloadTask.BatchDownloadTask -> it.taskList
                            is IDownloadTask.PlaylistDownloadTask -> it.taskList
                        }
                    }.associateBy { it.downloadTaskModel.relateEntityId!! }
                    val mapPagingItems = uiState.mapFlow.collectAsLazyPagingItems()
                    MapCardPagingList(
                        Modifier.weight(2f,false),
                        snackbarHostState = snackbarHostState,
                        mapPagingItems = mapPagingItems,
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
                        downloadingTask = downloadingTasks,
                    )
                }
                TabType.Playlist -> {
                    uiState as BeatSaverUiState.PlaylistQuery
                    val playlistPagingItems = uiState.playlistFlow.collectAsLazyPagingItems()
                    PlaylistPagingList(
                        Modifier.weight(2f,false),
                        snackbarHostState = snackbarHostState,
                        playlistPagingItems = playlistPagingItems,
                        localState = uiState.localState,
                        mapMultiSelectedMode = uiState.multiSelectMode,
                        mapMultiSelected = uiState.multiSelectedBSMap,
                        onUIEvent = onUIEvent,
                        stickyHeader = {
                        },
                        downloadingTask = emptyMap(),
                    )
                }
            }
        }
    }
//    SnackBarShown(
//        snackbarHostState = snackbarHostState,
//        snackBarMessages = uiState.snackBarMessages,
//        onSnackBarShown = onSnackBarShown,
//    )
}

@Composable
fun TextTabs(
    selectedTab : TabType,
    onClickTab: (TabType) -> Unit
) {
    Column {

        TabRow(selectedTabIndex = TabType.getIndexOf(selectedTab)) {
            TabType.tabs.forEachIndexed { index, tabType ->
                Tab(
                    selected = selectedTab == TabType.fromIndex(index),
                    onClick = { onClickTab(tabType) },
                    text = { Text(text = tabType.human, overflow = TextOverflow.Ellipsis) }
                )
            }
        }
    }
}