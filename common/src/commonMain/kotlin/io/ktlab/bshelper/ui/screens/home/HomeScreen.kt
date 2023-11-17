package io.ktlab.bshelper.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.rememberContentPaddingForScreen
import io.ktlab.bshelper.ui.components.BSHelperSnackbarHost
import io.ktlab.bshelper.ui.components.EmptyContent
import io.ktlab.bshelper.ui.components.SnackBarShown
import io.ktlab.bshelper.ui.components.VerticalDivider
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.home.bsmap.MapCardList
import io.ktlab.bshelper.ui.screens.home.playlist.PlaylistDetailCardTop
import io.ktlab.bshelper.ui.screens.home.playlist.PlaylistList
import io.ktlab.bshelper.ui.screens.home.playlist.PlaylistListTop
import io.ktlab.bshelper.viewmodel.HomeUIEvent
import io.ktlab.bshelper.viewmodel.HomeUiState
import io.ktlab.bshelper.viewmodel.HomeViewModel
import moe.tlaster.precompose.koin.koinViewModel

import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.successOr

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    onUIEvent: (UIEvent) -> Unit,
    modifier: Modifier = Modifier
){
    val snackbarHostState = remember { SnackbarHostState() }
    val contentPadding = rememberContentPaddingForScreen(
        additionalTop = if (showTopAppBar) 0.dp else 8.dp,
        excludeTop = showTopAppBar
    )

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    Scaffold(
        snackbarHost = { BSHelperSnackbarHost(hostState = snackbarHostState) },
        topBar = {},
        modifier = modifier
    ){ innerPadding ->
        val contentModifier = Modifier
            .padding(innerPadding)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
        when(uiState) {
            is HomeUiState.Playlist -> {
                HomeContent(
                    uiState,
                    onUIEvent,
                    contentModifier
                )
            }
        }

        SnackBarShown(
            snackbarHostState = snackbarHostState,
            snackBarMessages = uiState.snackBarMessages,
            onSnackBarShown = { onUIEvent(HomeUIEvent.SnackBarShown(it)) },
        )
    }
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    onUIEvent: (UIEvent) -> Unit,
    modifier: Modifier = Modifier,
){
    uiState as HomeUiState.Playlist
    val homeListLazyListState = rememberLazyListState()
    Row(modifier) {
        PlaylistList(
            modifier = Modifier
                .weight(1f),
            //                    .notifyInput(),
            //                contentPadding = contentPadding,
            state = homeListLazyListState,
            playlists = uiState.playlists,
            selectedPlaylist = uiState.selectedPlaylist,
            onUIEvent = onUIEvent,
            stickyHeader = {
                PlaylistListTop(
                    onUIEvent = onUIEvent,
                    selectablePlaylists = uiState.playlists,
                )
            }
        )
        VerticalDivider()
        HomeRightPart(
            uiState = uiState,
            onUIEvent = onUIEvent,
            modifier = Modifier.fillMaxHeight().weight(2f,false),
        )
    }
}

@Composable
fun HomeRightPart(
    uiState: HomeUiState.Playlist,
    modifier: Modifier,
    onUIEvent: (UIEvent) -> Unit,
) {

    Box(modifier) {
        if (uiState.isMapEmpty()) {
            EmptyContent()
        }else {
            val mapList = if (uiState.mapListState.mapFlow == null) {
                emptyList()
            } else {
                uiState.mapListState.mapFlow.
                collectAsState(Result.Success(emptyList())).value.successOr(emptyList())
            }
            val detailPlaylist = uiState.selectedPlaylist!!
            key(detailPlaylist.id) {
                MapCardList(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize(),
                    mapListState = uiState.mapListState,
                    onUIEvent = onUIEvent,
                    mapList = mapList,
                    stickyHeader = {
                        PlaylistDetailCardTop(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            playlist = detailPlaylist,
                            mapListState = uiState.mapListState,
                            mapList = mapList,
                            selectablePlaylists = uiState.playlists.filter { it.id != detailPlaylist.id },
                            onUIEvent = onUIEvent,
                        )
                        Divider(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                    }
                )
            }
        }
    }
}