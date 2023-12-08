package io.ktlab.bshelper.ui.route

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.rememberContentPaddingForScreen
import io.ktlab.bshelper.ui.components.BSHelperSnackbarHost
import io.ktlab.bshelper.ui.screens.beatsaver.BSMapScreen
import io.ktlab.bshelper.ui.screens.beatsaver.BSPlaylistScreen
import io.ktlab.bshelper.ui.screens.beatsaver.components.TextTabs
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverViewModel
import io.ktlab.bshelper.viewmodel.TabType
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeatSaverRoute(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    snackbarHost: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState,
){


    val beatSaverViewModel: BeatSaverViewModel = koinViewModel()
    val uiState by beatSaverViewModel.uiState.collectAsState()
    val showTopAppBar = !isExpandedScreen
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    Scaffold(
        snackbarHost = snackbarHost,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ){
                Row (
                    modifier = Modifier
                        .widthIn(Dp.Unspecified,300.dp)
                        .fillMaxWidth()
                ){
                    TextTabs(
                        selectedTab = uiState.tabType,
                        onClickTab = { beatSaverViewModel.dispatchUiEvents(BeatSaverUIEvent.SwitchTab(it)) }
                    )
                }
            }
        },
        modifier = Modifier
    ){innerPadding ->
        val contentModifier = Modifier
            .padding(innerPadding)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
        Surface(contentModifier) {
            Row {
                when (uiState.tabType) {
                    TabType.Map -> BSMapScreen(
                        uiState = uiState,
                        snackbarHostState = snackbarHostState,
                        onUIEvent = beatSaverViewModel::dispatchUiEvents,
                    )
                    TabType.Playlist -> BSPlaylistScreen(
                        uiState = uiState,
                        snackbarHostState = snackbarHostState,
                        onUIEvent = beatSaverViewModel::dispatchUiEvents
                    )
                }
            }

        }
    }
}