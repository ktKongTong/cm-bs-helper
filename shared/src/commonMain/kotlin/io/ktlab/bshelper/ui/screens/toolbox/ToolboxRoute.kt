package io.ktlab.bshelper.ui.screens.toolbox

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.ktlab.bshelper.ui.screens.toolbox.ToolboxScreen
import io.ktlab.bshelper.ui.viewmodel.GlobalUiState
import io.ktlab.bshelper.ui.viewmodel.ToolboxViewModel
import moe.tlaster.precompose.koin.koinViewModel

@Composable
fun ToolboxRoute(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    snackbarHost: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState,
    globalUiState: GlobalUiState,
    toolboxViewModel: ToolboxViewModel = koinViewModel<ToolboxViewModel>(),
) {
    val uiState by toolboxViewModel.uiState.collectAsState()
    ToolboxScreen(
        uiState = uiState,
        showTopAppBar = !isExpandedScreen,
        openDrawer = openDrawer,
        snackbarHost = snackbarHost,
    )
}
