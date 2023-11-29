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
import io.ktlab.bshelper.ui.screens.beatsaver.BeatSaverScreen
import io.ktlab.bshelper.ui.screens.beatsaver.TextTabs
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverViewModel
import io.ktlab.bshelper.viewmodel.TabType
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.RouteBuilder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeatSaverRoute(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    snackbarHost: @Composable () -> Unit = {},
    beatSaverViewModel: BeatSaverViewModel = koinViewModel()
){
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
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            }

//            if (showTopAppBar) {
//                BeatSaverTopAppBar(
//                    openDrawer = {  },
//                    topAppBarState = topAppBarState
//                )
//            }
        },
        modifier = Modifier
    ){innerPadding ->
        val contentModifier = Modifier
            .padding(innerPadding)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
        Surface(contentModifier) {
            Row(
                Modifier
            ) {
                when (uiState.tabType) {
                    TabType.Map -> BSMapScreen(
                        uiState = uiState,
                        snackbarHostState = remember { SnackbarHostState() },
                        onUIEvent = beatSaverViewModel::dispatchUiEvents
                    )
                    TabType.Playlist -> BSPlaylistScreen(
                        uiState = uiState,
                        snackbarHostState = remember { SnackbarHostState() },
                        onUIEvent = beatSaverViewModel::dispatchUiEvents
                    )
                }
            }
        }

    }



//    BeatSaverScreen(
//        uiState = uiState,
//        showTopAppBar = !isExpandedScreen,
//        openDrawer = openDrawer,
//        snackbarHostState = remember { SnackbarHostState() },
//        onUIEvent = beatSaverViewModel::dispatchUiEvents,
////        onSnackBarShown = beatSaverViewModel::snackBarShown,
//    )
}

fun RouteBuilder.beatSaverRouter(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
): Unit {

//    scene("/playlist"){
//        val uiState by beatSaverViewModel.uiState.collectAsState()
//        BeatSaverScreen(
//            uiState = uiState,
//            showTopAppBar = !isExpandedScreen,
//            openDrawer = openDrawer,
//            snackbarHostState = remember { SnackbarHostState() },
//            onUIEvent = beatSaverViewModel::dispatchUiEvents,
//            onSnackBarShown = beatSaverViewModel::snackBarShown,
//        )
//        scene("d") {
//
//        }
//    }
//    scene("/playlist/{id:[0-9]+}"){
//
//    }
//    scene("/map/{id:[0-9]+}"){
//
//    }
//    scene("/map"){
//
//    }
//    scene("/mapper"){
//
//    }
}