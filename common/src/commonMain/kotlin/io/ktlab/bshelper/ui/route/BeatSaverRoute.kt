package io.ktlab.bshelper.ui.route

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import io.ktlab.bshelper.ui.screens.beatsaver.BeatSaverScreen
import io.ktlab.bshelper.viewmodel.BeatSaverViewModel
import moe.tlaster.precompose.koin.koinViewModel

@Composable
fun BeatSaverRoute(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    beatSaverViewModel: BeatSaverViewModel = koinViewModel()
){
    val uiState by beatSaverViewModel.uiState.collectAsState()
    BeatSaverScreen(
        uiState = uiState,
        showTopAppBar = !isExpandedScreen,
        openDrawer = openDrawer,
        snackbarHostState = remember { SnackbarHostState() },
        onUIEvent = beatSaverViewModel::dispatchUiEvents,
        onSnackBarShown = beatSaverViewModel::snackBarShown,
    )
}