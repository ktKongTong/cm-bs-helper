package io.ktlab.bshelper.ui.route

import androidx.compose.runtime.Composable

@Composable
fun BeatSaverRoute(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
//    beatSaverViewModel: BeatSaverViewModel = hiltViewModel(),
){
//    val uiState by beatSaverViewModel.uiState.collectAsStateWithLifecycle()
//    BeatSaverScreen(
//        uiState = uiState,
//        showTopAppBar = !isExpandedScreen,
//        openDrawer = openDrawer,
//        snackbarHostState = snackbarHostState,
//        onUIEvent = beatSaverViewModel::dispatchUiEvents,
//        onSnackBarShown = beatSaverViewModel::snackBarShown,
//    )
}