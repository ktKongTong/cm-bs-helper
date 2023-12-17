package io.ktlab.bshelper


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.ui.components.*
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.route.BSHelperDestinations
import io.ktlab.bshelper.ui.route.BSHelperNavGraph
import io.ktlab.bshelper.ui.route.BSHelperNavigationActions
import io.ktlab.bshelper.ui.theme.BSHelperTheme
import io.ktlab.bshelper.ui.theme.defaultThemeSeedColor
import io.ktlab.bshelper.viewmodel.ErrorDialogState
import io.ktlab.bshelper.viewmodel.GlobalUIEvent
import io.ktlab.bshelper.viewmodel.GlobalViewModel
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.rememberNavigator
import org.koin.compose.KoinContext

val LocalUIEventHandler = staticCompositionLocalOf<((UIEvent) -> Unit)> { {} }
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BSHelperApp(){
    KoinContext {

        val globalViewModel = koinViewModel<GlobalViewModel>()
        val globalUiState by globalViewModel.uiState.collectAsState()
//        globalViewModel
        // get
        val color = globalUiState.userPreference.getThemeColor()?.let { Color(it) } ?: defaultThemeSeedColor
        BSHelperTheme(color) {
            val navigator = rememberNavigator()
            val navigationActions = remember(navigator) {
                BSHelperNavigationActions(navigator)
            }
            val navigateAction = fun(navDestination: String) {
                when (navDestination) {
                    BSHelperDestinations.HOME_ROUTE -> navigationActions.navigateToHome()
                    BSHelperDestinations.BEAT_SAVER_ROUTE -> navigationActions.navigateToBeatSaver()
                    BSHelperDestinations.TOOLBOX_ROUTE -> navigationActions.navigateToToolbox()
                }
            }


            val windowSizeClass = calculateWindowSizeClass().widthSizeClass
            val coroutineScope = rememberCoroutineScope()

            val navBackStackEntry = navigator.currentEntry.collectAsState(null)
            val currentRoute = navBackStackEntry.value?.route?.route ?: BSHelperDestinations.HOME_ROUTE

            val isExpandedScreen =
                (windowSizeClass == WindowWidthSizeClass.Expanded) || (windowSizeClass == WindowWidthSizeClass.Medium)
            val sizeAwareDrawerState = rememberSizeAwareDrawerState(isExpandedScreen)


            CompositionLocalProvider( LocalUIEventHandler provides globalViewModel::dispatchUiEvents) {
                ModalNavigationDrawer(
                    drawerContent = {
                        AppDrawer(
                            currentRoute = currentRoute,
                            navigateAction = navigateAction,
                            closeDrawer = { coroutineScope.launch { sizeAwareDrawerState.close() } }
                        )
                    },
                    drawerState = sizeAwareDrawerState,
                    // Only enable opening the drawer via gestures if the screen is not expanded
                    gesturesEnabled = !isExpandedScreen
                ) {

                        Row {
                            if (isExpandedScreen) {
                                AppNavRail(
                                    currentRoute = currentRoute,
                                    navigateAction = navigateAction,
                                    backAction = { },
                                    header = {
                                        MediaPlayer(globalUiState.currentMedia,globalUiState.currentMediaState,globalViewModel::dispatchUiEvents)
                                    }
                                )
                            }
                            val snackbarHostState = remember { SnackbarHostState() }
                            BSHelperNavGraph(
                                isExpandedScreen = isExpandedScreen,
                                navigator = navigator,
                                openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                                snackbarHost = { BSHelperSnackbarHost(hostState = snackbarHostState) },
                                snackbarHostState = snackbarHostState,
                                globalUiState = globalUiState,
                            )
                            ErrorReportDialog(errorDialogState = globalUiState.errorDialogState)
                            //
                            SnackBarShown(
                                snackbarHostState = snackbarHostState,
                                snackBarMessages = globalUiState.snackBarMessages,
                                onSnackBarShown = {
                                    globalViewModel.dispatchUiEvents(GlobalUIEvent.SnackBarShown(it))
                                },
                            )
                         }

            }

            }
        }
    }
}

@Composable
private fun ErrorReportDialog(
    errorDialogState: ErrorDialogState?,
) {
    if (errorDialogState == null) {
        return
    }
    AlertDialog(
        onDismissRequest = {
            errorDialogState.onCancel?.let { it() }
        },
        title = { Text(text = errorDialogState.title) },
        text = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(max = 400.dp)
            ) {
                Text(modifier = Modifier.verticalScroll(rememberScrollState()), text = errorDialogState.message)
            }
        },
        confirmButton = {
            TextButton(
                onClick = { errorDialogState.onConfirm?.let { it() } }
            ) { Text(errorDialogState.confirmLabel!!) }
        },
        dismissButton = {
            if(errorDialogState.onCancel != null) {
                TextButton(
                    onClick = { errorDialogState.onCancel?.let { it() } }
                ) { Text(errorDialogState.cancelLabel!!) }
            }
        }
    )
}


/**
 * Determine the drawer state to pass to the modal drawer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun rememberSizeAwareDrawerState(isExpandedScreen: Boolean): DrawerState {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    return if (!isExpandedScreen) {
        // If we want to allow showing the drawer, we use a real, remembered drawer
        // state defined above
        drawerState
    } else {
        // If we don't want to allow the drawer to be shown, we provide a drawer state
        // that is locked closed. This is intentionally not remembered, because we
        // don't want to keep track of any changes and always keep it closed
        DrawerState(DrawerValue.Closed)
    }
}

/**
 * Determine the content padding to apply to the different screens of the app
 */
@Composable
fun rememberContentPaddingForScreen(
    additionalTop: Dp = 0.dp,
    excludeTop: Boolean = false
) = WindowInsets.systemBars
    .only(if (excludeTop) WindowInsetsSides.Bottom else WindowInsetsSides.Vertical)
    .add(WindowInsets(top = additionalTop)).asPaddingValues()
