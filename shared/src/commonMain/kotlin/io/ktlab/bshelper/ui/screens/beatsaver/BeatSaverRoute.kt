package io.ktlab.bshelper.ui.screens.beatsaver

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.ui.LocalUIEventHandler
import io.ktlab.bshelper.ui.components.DropDownPlaylistSelectorV2
import io.ktlab.bshelper.ui.event.BeatSaverUIEvent
import io.ktlab.bshelper.ui.screens.beatsaver.components.BSMapDetail
import io.ktlab.bshelper.ui.screens.beatsaver.components.BSMapperDetail
import io.ktlab.bshelper.ui.screens.beatsaver.components.TextTabs
import io.ktlab.bshelper.ui.viewmodel.BeatSaverViewModel
import io.ktlab.bshelper.ui.viewmodel.TabType
import moe.tlaster.precompose.koin.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun BeatSaverRoute(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    snackbarHost: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState,
) {
    val beatSaverViewModel: BeatSaverViewModel = koinViewModel()
    val uiState by beatSaverViewModel.uiState.collectAsState()
    val showTopAppBar = !isExpandedScreen
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    Scaffold(
        snackbarHost = snackbarHost,
        topBar = {
                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,

                ) {

                    val onUIEvent = LocalUIEventHandler.current
                    TextTabs(
                        modifier = Modifier.padding(8.dp).widthIn(Dp.Unspecified, 300.dp),
                        selectedTab = uiState.tabType,
                        onClickTab = { onUIEvent(BeatSaverUIEvent.SwitchTab(it)) },
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        val selectablePlaylists = uiState.localState.selectableLocalPlaylistFlow.collectAsState(emptyList())
                        DropDownPlaylistSelectorV2(
                            onUIEvent = onUIEvent,
                            modifier = Modifier.padding(8.dp),
                            selectablePlaylists = selectablePlaylists.value,
                            selectedIPlaylist = uiState.localState.targetPlaylist,
                            onSelectedPlaylist = { onUIEvent(BeatSaverUIEvent.ChangeTargetPlaylist(it)) },
                        )
                    }
                }

        },
        modifier = Modifier,
    ) { innerPadding ->
        val contentModifier =
            Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)

        AnimatedContent(
            targetState = uiState.tabType,
            transitionSpec = {
                (
                    fadeIn() +
                        slideInHorizontally(
                            animationSpec = tween(400),
                            initialOffsetX = {
                                if (targetState < initialState) {
                                    -it
                                } else {
                                    it
                                }
                            },
                        )
                ).togetherWith(
                    fadeOut() +
                        slideOutHorizontally(
                            animationSpec = tween(400),
                            targetOffsetX = {
                                if (targetState < initialState) {
                                    it
                                } else {
                                    -it
                                }
                            },
                        ),
                )
            },
        ) { targetState ->
            val onUIEvent = LocalUIEventHandler.current
            Surface(contentModifier) {
                val lazyMapGridState = rememberLazyGridState()
                val mapperLazyGridState = rememberLazyGridState()
                val playlistLazyGridState = rememberLazyListState()
                if (uiState.isLoading) {
                    LinearProgressIndicator(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(2.dp),
                        strokeCap = StrokeCap.Round,
                    )
                }
                // with animation, targetState changes, but still not working
                when (targetState) {
                    TabType.Map ->
                        BSMapScreen(
                            uiState = uiState,
                            snackbarHostState = snackbarHostState,
                            onUIEvent = onUIEvent,
                            lazyGridState = lazyMapGridState,
                        )
                    TabType.Playlist ->
                        BSPlaylistScreen(
                            uiState = uiState,
                            snackbarHostState = snackbarHostState,
                            onUIEvent = onUIEvent,
                            lazyListState = playlistLazyGridState,
                        )
                    TabType.Mapper -> {
                        BSMapperScreen(
                            uiState = uiState,
                            snackbarHostState = snackbarHostState,
                            onUIEvent = onUIEvent,
                            lazyGridState = mapperLazyGridState,
                        )
                    }
                }
                uiState.selectedBSMapper?.let {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        BSMapperDetail(
                            uiState = uiState,
                            snackbarHostState = snackbarHostState,
                            onUIEvent = onUIEvent,
                            mapFlow = uiState.selectedBSMapperMapFlow,
                            localState = uiState.localState,
                        )
                    }
                }
                uiState.selectedBSMap?.let {
                    AnimatedVisibility(uiState.selectedBSMap != null) {
                        Surface(modifier = Modifier.fillMaxSize()) {
                            uiState.selectedBSMap?.let {
                                BSMapDetail(
                                    map = uiState.selectedBSMap!!,
                                    onUIEvent = onUIEvent,
                                    comments = uiState.selectedBSMapReview,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
