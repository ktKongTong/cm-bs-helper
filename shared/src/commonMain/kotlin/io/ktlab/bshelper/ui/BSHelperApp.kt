package io.ktlab.bshelper.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.BuildConfig
import io.ktlab.bshelper.model.AppVersionChangeLog
import io.ktlab.bshelper.model.ThemeMode
import io.ktlab.bshelper.model.UserPreferenceV2
import io.ktlab.bshelper.ui.components.AppDrawer
import io.ktlab.bshelper.ui.components.AppNavRail
import io.ktlab.bshelper.ui.components.BSHelperSnackbarHost
import io.ktlab.bshelper.ui.components.MediaPlayer
import io.ktlab.bshelper.ui.components.SnackBarShown
import io.ktlab.bshelper.ui.composables.OpenInBrowser
import io.ktlab.bshelper.ui.event.EventBus
import io.ktlab.bshelper.ui.event.GlobalUIEvent
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.route.BSHelperDestinations
import io.ktlab.bshelper.ui.route.BSHelperNavGraph
import io.ktlab.bshelper.ui.route.BSHelperNavigationActions
import io.ktlab.bshelper.ui.theme.BSHelperTheme
import io.ktlab.bshelper.ui.viewmodel.AppVersionDialogState
import io.ktlab.bshelper.ui.viewmodel.ErrorDialogState
import io.ktlab.bshelper.ui.viewmodel.GlobalViewModel
import kotlinx.coroutines.launch
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.rememberNavigator
import org.koin.compose.KoinContext


val LocalUIEventHandler = staticCompositionLocalOf<((UIEvent) -> Unit)> { {} }

val LocalUserPreference = compositionLocalOf<UserPreferenceV2>{ error("No user preference provided") }

val LocalSizeClass = compositionLocalOf<WindowWidthSizeClass> { error("No size class provided") }


fun WindowWidthSizeClass.isExpandedScreen() = this == WindowWidthSizeClass.Expanded || this == WindowWidthSizeClass.Medium

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun BSHelperApp() {
    PreComposeApp {
        KoinContext {
            val globalViewModel = koinViewModel<GlobalViewModel>()
//            val eventBus = koinEventBus()
            val globalUiState by globalViewModel.uiState.collectAsState()
            val color = globalUiState.userPreference.themeColor.let { Color(it) }
            // TODO: provider some global state
            // like sizeInfo, sizeClass, platform info

            val coroutineScope = rememberCoroutineScope()
            val dispatchUIEvent = remember { fun(it: UIEvent) { coroutineScope.launch { EventBus.publish(it) } } }
            val windowSizeClass = calculateWindowSizeClass().widthSizeClass
            val isExpandedScreen = windowSizeClass.isExpandedScreen()
            CompositionLocalProvider(LocalUIEventHandler provides dispatchUIEvent) {
            CompositionLocalProvider(LocalUserPreference provides globalUiState.userPreference) {
            CompositionLocalProvider(LocalSizeClass provides windowSizeClass) {
                    val isDarkMode = globalUiState.userPreference.themeMode == ThemeMode.DARK || isSystemInDarkTheme()
                    BSHelperTheme(color, darkTheme = isDarkMode) {
                        val navigator = rememberNavigator()
                        val navigationActions = remember(navigator) { BSHelperNavigationActions(navigator) }
                        val navigateAction = fun(navDestination: String) {
                            when (navDestination) {
                                BSHelperDestinations.HOME_ROUTE -> navigationActions.navigateToHome()
                                BSHelperDestinations.BEAT_SAVER_ROUTE -> navigationActions.navigateToBeatSaver()
                                BSHelperDestinations.TOOLBOX_ROUTE -> navigationActions.navigateToToolbox()
                            }
                        }
                        val navBackStackEntry = navigator.currentEntry.collectAsState(null)
                        val currentRoute = navBackStackEntry.value?.route?.route ?: BSHelperDestinations.HOME_ROUTE

                        val sizeAwareDrawerState = rememberSizeAwareDrawerState(isExpandedScreen)
                        ModalNavigationDrawer(
                            drawerContent = {
                                AppDrawer(
                                    currentRoute = currentRoute,
                                    navigateAction = navigateAction,
                                    closeDrawer = { coroutineScope.launch { sizeAwareDrawerState.close() } },
                                )
                            },
                            drawerState = sizeAwareDrawerState,
                            gesturesEnabled = !isExpandedScreen,
                        ) {
                            Row {
                                if (isExpandedScreen) {
                                    AppNavRail(
                                        currentRoute = currentRoute,
                                        navigateAction = navigateAction,
                                        backAction = { },
                                        currentManageFolder = globalUiState.userPreference.currentManageFolder,
                                        manageFolders = globalUiState.manageDirs,
                                        header = {
                                            MediaPlayer(globalUiState.currentMedia, globalUiState.currentMediaState)
                                        },
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
                                AppVersionReportDialog(appVersionDialogState = globalUiState.appVersionDialogState)
                                ErrorReportDialog(errorDialogState = globalUiState.errorDialogState)
                                SnackBarShown(
                                    snackbarHostState = snackbarHostState,
                                    snackBarMessages = globalUiState.snackBarMessages,
                                    onSnackBarShown = { dispatchUIEvent(GlobalUIEvent.SnackBarShown(it)) },
                                )
                            }
                        }

                    }
                }
            }
            }
        }
    }
}
@Composable
private fun ChangeLogItem(
    changeLog: AppVersionChangeLog,
){
    Column(
        Modifier.fillMaxWidth().padding(16.dp,8.dp)
    ) {
        Row (
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column {
                Text(text = changeLog.version, style = MaterialTheme.typography.titleLarge)
                Text(text = changeLog.date, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.weight(1f,false))
            val show = remember { mutableStateOf(false) }
            if(show.value) {
                OpenInBrowser(changeLog.url)
            }
            TextButton(onClick = { show.value = true }) { Text("前去下载") }
        }
        Text(text = changeLog.releaseNote, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun AppVersionReportDialog(appVersionDialogState: AppVersionDialogState?) {
    if(appVersionDialogState == null){
        return
    }
    AlertDialog(
        onDismissRequest = {
            appVersionDialogState.onCancel?.let { it() }
        },
        title = { Column {
            Text(text = appVersionDialogState.title)
            Text(text = BuildConfig.APP_VERSION, style = MaterialTheme.typography.labelSmall)
        } },
        text = {
            Column(
                modifier =
                    Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                        .heightIn(max = 400.dp),
            ) {
                appVersionDialogState.changeLogs.forEach {
                    ChangeLogItem(it)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { appVersionDialogState.onConfirm?.let { it() } },
            ) { Text(appVersionDialogState.confirmLabel!!) }
        },
        dismissButton = {},
    )
}
@Composable
private fun ErrorReportDialog(errorDialogState: ErrorDialogState?) {
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
                modifier =
                    Modifier
                        .padding(16.dp)
                        .heightIn(max = 400.dp),
            ) {
                Text(modifier = Modifier.verticalScroll(rememberScrollState()), text = errorDialogState.message)
            }
        },
        confirmButton = {
            TextButton(
                onClick = { errorDialogState.onConfirm?.let { it() } },
            ) { Text(errorDialogState.confirmLabel!!) }
        },
        dismissButton = {
            if (errorDialogState.onCancel != null) {
                TextButton(
                    onClick = { errorDialogState.onCancel?.let { it() } },
                ) { Text(errorDialogState.cancelLabel!!) }
            }
        },
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
    excludeTop: Boolean = false,
) = WindowInsets.systemBars
    .only(if (excludeTop) WindowInsetsSides.Bottom else WindowInsetsSides.Vertical)
    .add(WindowInsets(top = additionalTop)).asPaddingValues()
