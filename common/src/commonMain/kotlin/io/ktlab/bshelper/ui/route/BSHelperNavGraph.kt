package io.ktlab.bshelper.ui.route

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition

@Composable
fun BSHelperNavGraph(
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    navigator:Navigator = rememberNavigator(),
    openDrawer: () -> Unit = {},
    snackbarHost : @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState,
    startDestination: String = BSHelperDestinations.HOME_ROUTE,
){
    NavHost(
        navigator = navigator,
        initialRoute = startDestination,
        navTransition = NavTransition(),
        modifier = modifier
    ) {
        scene(BSHelperDestinations.HOME_ROUTE) {
            HomeRoute(
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer,
                snackbarHost = snackbarHost
            )
        }
        scene(BSHelperDestinations.BEAT_SAVER_ROUTE) {
            BeatSaverRoute(
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer,
                snackbarHost = snackbarHost,
                snackbarHostState = snackbarHostState
            )
        }
        scene(BSHelperDestinations.TOOLBOX_ROUTE) {
            ToolboxRoute(
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer,
                snackbarHost = snackbarHost
            )
        }
    }
}