package io.ktlab.bshelper.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.successOr
import io.ktlab.bshelper.ui.components.EmptyContent
import io.ktlab.bshelper.ui.screens.home.bsmap.MapCardList
import io.ktlab.bshelper.ui.screens.home.playlist.PlaylistDetailCardTop
import io.ktlab.bshelper.ui.screens.home.playlist.PlaylistList
import io.ktlab.bshelper.ui.viewmodel.HomeUiState

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    snackbarHost: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    Scaffold(
        snackbarHost = snackbarHost,
        topBar = {},
        modifier = modifier,
    ) { innerPadding ->
        val contentModifier =
            Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        when (uiState) {
            is HomeUiState.Playlist -> {
                HomeContent(
                    uiState,
                    contentModifier,
                )
            }
        }
    }
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
) {
    // todo:
    // when selectedPlaylist is null, only show playlist list
    // can cover full screen, use grid layout with multiple columns
    // when select a playlist, show map list, use animation, shrink playlist list to left
    //
    uiState as HomeUiState.Playlist
    val homeListLazyListState = rememberLazyListState()
    Row(modifier) {
        val playlistModifier =
            if (uiState.selectedPlaylist != null) {
                Modifier
                    .animateContentSize()
                    .widthIn(Dp.Unspecified, 300.dp)
            } else {
                Modifier.animateContentSize()
            }
        PlaylistList(
            modifier = playlistModifier,
            state = homeListLazyListState,
            playlists = uiState.playlists,
            selectedPlaylist = uiState.selectedPlaylist,
        )
        AnimatedVisibility(
            uiState.selectedPlaylist != null,
            enter = (
                fadeIn() +
                    slideInHorizontally(
                        animationSpec = tween(400),
                        initialOffsetX = { fullWidth -> fullWidth },
                    )
            ),
        ) {
            HomeRightPart(
                uiState = uiState,
                modifier = Modifier.fillMaxHeight(),
            )
        }
    }
}

@Composable
fun HomeRightPart(
    uiState: HomeUiState.Playlist,
    modifier: Modifier,
) {
    Box(modifier) {
        if (uiState.isMapEmpty()) {
            EmptyContent()
        } else {
            val mapList =
                if (uiState.mapListState.mapFlow == null) {
                    emptyList()
                } else {
                    uiState.mapListState.mapFlow
                        .collectAsState(Result.Success(emptyList())).value.successOr(emptyList())
                }
            val detailPlaylist = uiState.selectedPlaylist!!
            AnimatedContent(
                detailPlaylist,
                transitionSpec = {
                    (
                        fadeIn() +
                            slideInVertically(
                                animationSpec = tween(400),
                                initialOffsetY = {
                                    if (targetState.title < initialState.title) {
                                        -it
                                    } else {
                                        it
                                    }
                                },
                            )
                    ).togetherWith(
                        fadeOut() +
                            slideOutVertically(
                                animationSpec = tween(400),
                                targetOffsetY = {
                                    if (targetState.title < initialState.title) {
                                        it
                                    } else {
                                        -it
                                    }
                                },
                            ),
                    )
                },
            ) {
                key(it.id) {
                    MapCardList(
                        modifier =
                            Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxSize(),
                        mapListState = uiState.mapListState,
                        mapList = mapList,
                        stickyHeader = {
                            PlaylistDetailCardTop(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                playlist = detailPlaylist,
                                mapListState = uiState.mapListState,
                                mapList = mapList,
                                selectablePlaylists = uiState.playlists.filter { it.id != detailPlaylist.id },
                            )
                        },
                    )
                }
            }
        }
    }
}
