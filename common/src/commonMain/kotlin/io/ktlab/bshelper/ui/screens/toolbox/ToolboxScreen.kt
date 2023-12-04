package io.ktlab.bshelper.ui.screens.toolbox

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.ui.components.Developing
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.ToolboxUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolboxScreen(
    uiState: ToolboxUiState,
    showTopAppBar: Boolean,
    snackbarHost: @Composable () -> Unit = {},
    openDrawer: () -> Unit,
    onUIEvent: (UIEvent) -> Unit,

//    onSnackBarShown: (Long) -> Unit,
    modifier: Modifier = Modifier
){
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    Scaffold(
        snackbarHost = snackbarHost,
        topBar = {
//            if (showTopAppBar) {
//               ToolboxTopAppBar(
//                    openDrawer = openDrawer,
//                    topAppBarState = topAppBarState
//                )
//            }
        },
        modifier = modifier
    ) { innerPadding ->
        val contentModifier = Modifier
            .padding(innerPadding)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
        Row (contentModifier){
            var selectedPage by remember { mutableStateOf(if(!showTopAppBar){ToolboxPage.Toolbox}else{ToolboxPage.Toolbox}) }
            ToolboxLeftSide(
                modifier = Modifier
                    .widthIn(Dp.Unspecified, 300.dp),
                selectedPage = selectedPage,
                onSelectedPage = { selectedPage = it }
            )
//            if(!showTopAppBar){
                ToolboxRightSide(
                    modifier = Modifier
                        .fillMaxWidth(),
                    selectedPage = selectedPage,
                    uiState = uiState,
                    onUIEvent = onUIEvent
                )
//            }
        }
    }

}
enum class ToolboxPage {
    None,
    Toolbox,
    Settings,
    About,
    Downloader,
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolboxLeftSide(
    modifier: Modifier = Modifier,
    selectedPage: ToolboxPage,
    onSelectedPage: (ToolboxPage) -> Unit
){
    ModalDrawerSheet(
        modifier,
        drawerContainerColor = Color.Transparent,
    ) {
        NavigationDrawerItem(
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent,
            ),
            label = { Text(stringResource(MR.strings.toolbox_title)) },
            icon = { Icon(Icons.Filled.Home, null) },
            selected = selectedPage == ToolboxPage.Toolbox,
            onClick = { onSelectedPage(ToolboxPage.Toolbox) },
            modifier = Modifier.padding(PaddingValues(vertical = 2.dp, horizontal = 12.dp))
        )
        NavigationDrawerItem(
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent,
            ),
            label = { Text(stringResource(MR.strings.settings)) },
            icon = { Icon(Icons.Filled.Settings, null) },
            selected = selectedPage == ToolboxPage.Settings,
            onClick = { onSelectedPage(ToolboxPage.Settings) },
            modifier = Modifier.padding(PaddingValues(vertical = 2.dp, horizontal = 12.dp))
        )
        NavigationDrawerItem(
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent,
            ),
            label = { Text(stringResource(MR.strings.downloader)) },
            icon = { Icon(Icons.Filled.Download, null) },
            selected = selectedPage == ToolboxPage.Downloader,
            onClick = { onSelectedPage(ToolboxPage.Downloader) },
            modifier = Modifier.padding(PaddingValues(vertical = 2.dp, horizontal = 12.dp))
        )
    }
}

@Composable
fun ToolboxRightSide(
    modifier: Modifier = Modifier,
    selectedPage: ToolboxPage,
    uiState: ToolboxUiState,
    onUIEvent: (UIEvent) -> Unit
){
    Surface(
        modifier = modifier
    ) {
        when (selectedPage) {
            ToolboxPage.Toolbox -> {
                ScanScreen(
                    scanState = uiState.scanState,
                    onUIEvent = onUIEvent
                )
            }
            ToolboxPage.About -> {
                Developing()
            }
            ToolboxPage.Settings -> {
                Developing()
//                SettingScreen(
//                    userPreference = uiState.userPreferenceState,
//                    onUIEvent = onUIEvent
//                )
            }
            ToolboxPage.Downloader -> {
                DownloadTaskScreen(
                    onUIEvent = onUIEvent,
                    downloadTasks = uiState.downloadTasks
                )
            }
            else -> {
                Developing()
            }
        }
    }
}