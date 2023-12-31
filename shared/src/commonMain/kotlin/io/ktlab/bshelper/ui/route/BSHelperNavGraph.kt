package io.ktlab.bshelper.ui.route

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ktlab.bshelper.ui.screens.beatsaver.BeatSaverRoute
import io.ktlab.bshelper.ui.screens.home.HomeRoute
import io.ktlab.bshelper.ui.screens.toolbox.ToolboxRoute
import io.ktlab.bshelper.ui.viewmodel.GlobalUiState
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition

@Composable
fun BSHelperNavGraph(
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    navigator: Navigator = rememberNavigator(),
    openDrawer: () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState,
    globalUiState: GlobalUiState,
    startDestination: String = BSHelperDestinations.HOME_ROUTE,
) {
    NavHost(
        navigator = navigator,
        initialRoute = startDestination,
        navTransition = NavTransition(),
        modifier = modifier,
    ) {
        scene(BSHelperDestinations.HOME_ROUTE) {
            HomeRoute(
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer,
                snackbarHost = snackbarHost,
                globalUiState = globalUiState,
                snackbarHostState = snackbarHostState,
            )
        }
        scene(BSHelperDestinations.BEAT_SAVER_ROUTE) {
            BeatSaverRoute(
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer,
                snackbarHost = snackbarHost,
                snackbarHostState = snackbarHostState,
            )
        }
        scene(BSHelperDestinations.TOOLBOX_ROUTE) {
            ToolboxRoute(
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer,
                snackbarHost = snackbarHost,
                globalUiState = globalUiState,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}
