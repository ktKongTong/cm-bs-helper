package io.ktlab.bshelper.ui.route

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import io.ktlab.bshelper.ui.screens.toolbox.ToolboxScreen
import io.ktlab.bshelper.viewmodel.ToolboxViewModel
import moe.tlaster.precompose.koin.koinViewModel

@Composable
fun ToolboxRoute(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    toolboxViewModel: ToolboxViewModel = koinViewModel<ToolboxViewModel>()
){

    val uiState by toolboxViewModel.uiState.collectAsState()
    ToolboxScreen(
        uiState = uiState,
        showTopAppBar = !isExpandedScreen,
        openDrawer = openDrawer,
        onUIEvent = toolboxViewModel::dispatchUiEvents,
        snackbarHostState = remember { SnackbarHostState() },
        onSnackBarShown = toolboxViewModel::snackBarShown,
    )
}