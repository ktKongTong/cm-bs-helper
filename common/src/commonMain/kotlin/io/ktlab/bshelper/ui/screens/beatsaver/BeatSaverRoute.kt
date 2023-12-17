package io.ktlab.bshelper.ui.screens.beatsaver

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.ui.screens.beatsaver.components.BSMapDetail
import io.ktlab.bshelper.ui.screens.beatsaver.components.BSMapperDetail
import io.ktlab.bshelper.ui.screens.beatsaver.components.TextTabs
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverViewModel
import io.ktlab.bshelper.viewmodel.GlobalUiState
import io.ktlab.bshelper.viewmodel.TabType
import moe.tlaster.precompose.koin.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun BeatSaverRoute(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    snackbarHost: @Composable () -> Unit = {},
    globalUiState: GlobalUiState,
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
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface),
            ) {
                Row(
                    modifier =
                        Modifier
                            .widthIn(Dp.Unspecified, 300.dp)
                            .fillMaxWidth(),
                ) {
                    TextTabs(
                        selectedTab = uiState.tabType,
                        onClickTab = { beatSaverViewModel.dispatchUiEvents(BeatSaverUIEvent.SwitchTab(it)) },
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
                            onUIEvent = beatSaverViewModel::dispatchUiEvents,
                            lazyGridState = lazyMapGridState,
                        )
                    TabType.Playlist ->
                        BSPlaylistScreen(
                            uiState = uiState,
                            snackbarHostState = snackbarHostState,
                            onUIEvent = beatSaverViewModel::dispatchUiEvents,
                            lazyListState = playlistLazyGridState,
                        )
                    TabType.Mapper -> {
                        BSMapperScreen(
                            uiState = uiState,
                            snackbarHostState = snackbarHostState,
                            onUIEvent = beatSaverViewModel::dispatchUiEvents,
                            lazyGridState = mapperLazyGridState,
                        )
                    }
                }
                uiState.selectedBSMap?.let {
                    AnimatedVisibility(uiState.selectedBSMap != null) {
//                transition
                        Surface(modifier = Modifier.fillMaxSize()) {
                            uiState.selectedBSMap?.let {
                                BSMapDetail(
                                    map = uiState.selectedBSMap!!,
                                    onUIEvent = beatSaverViewModel::dispatchUiEvents,
                                    comments = uiState.selectedBSMapReview,
                                )
                            }
                        }
                    }
                    return@Surface
                }
                uiState.selectedBSMapper?.let {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        BSMapperDetail(
                            uiState = uiState,
                            snackbarHostState = snackbarHostState,
                            onUIEvent = beatSaverViewModel::dispatchUiEvents,
                            mapFlow = uiState.selectedBSMapperMapFlow,
                            localState = uiState.localState,
                        )
                    }
                }
            }
        }
    }
}
