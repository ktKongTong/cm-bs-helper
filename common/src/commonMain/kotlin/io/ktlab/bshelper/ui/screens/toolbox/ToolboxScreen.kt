package io.ktlab.bshelper.ui.screens.toolbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.ui.components.BSHelperSnackbarHost
import io.ktlab.bshelper.ui.components.Developing
import io.ktlab.bshelper.ui.components.SnackBarShown
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.ToolboxUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolboxScreen(
    uiState: ToolboxUiState,
    showTopAppBar: Boolean,
    snackbarHostState: SnackbarHostState,
    openDrawer: () -> Unit,
    onUIEvent: (UIEvent) -> Unit,
    onSnackBarShown: (Long) -> Unit,
    modifier: Modifier = Modifier
){
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    Scaffold(
        snackbarHost = { BSHelperSnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (showTopAppBar) {
//               ToolboxTopAppBar(
//                    openDrawer = openDrawer,
//                    topAppBarState = topAppBarState
//                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        val contentModifier = Modifier
            .padding(innerPadding)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
        Row (contentModifier){
            var selectedPage by remember { mutableStateOf(if(!showTopAppBar){ToolboxPage.Toolbox}else{ToolboxPage.None}) }
            ToolboxLeftSide(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f,fill = false),
                selectedPage = selectedPage,
                onSelectedPage = { selectedPage = it }
            )
//            if(!showTopAppBar){

                Divider(
                    thickness = 2.dp,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 20.dp)
                        .width(1.dp)
                )
                ToolboxRightSide(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f),
                    selectedPage = selectedPage,
                    uiState = uiState,
                    onUIEvent = onUIEvent
                )
//            }
        }
    }

    SnackBarShown(
        snackbarHostState = snackbarHostState,
        snackBarMessages = uiState.snackBarMessages,
        onSnackBarShown = onSnackBarShown,
    )

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
                Developing()
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
                Developing()
//                DownloadTaskScreen(
//                    onUIEvent = onUIEvent,
//                    downloadTasks = uiState.downloadTasks
//                )
            }
            else -> {
                Developing()
            }
        }
    }
}