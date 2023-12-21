package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.model.SManageFolder
import io.ktlab.bshelper.ui.LocalUIEventHandler
import io.ktlab.bshelper.ui.route.BSHelperDestinations
import io.ktlab.bshelper.MR as R

@Composable
fun AppNavRail(
    currentRoute: String,
    navigateAction: (String) -> Unit,
    currentManageFolder: SManageFolder? = null,
    manageFolders:List<SManageFolder>,
    backAction: () -> Unit = {},
    modifier: Modifier = Modifier,
    header: @Composable ColumnScope.() -> Unit = {},
) {
    val onUIEvent = LocalUIEventHandler.current
    NavigationRail(
        header = header,
        modifier = modifier,
    ) {
        Spacer(Modifier.weight(1f))
        NavigationRailItem(
            selected = currentRoute == BSHelperDestinations.HOME_ROUTE,
            onClick = { if (currentRoute != BSHelperDestinations.HOME_ROUTE) navigateAction(BSHelperDestinations.HOME_ROUTE) },
            icon = { Icon(Icons.Filled.Home, stringResource(R.strings.home_title)) },
            label = { Text(stringResource(R.strings.home_title)) },
            alwaysShowLabel = false,
        )
        NavigationRailItem(
            selected = currentRoute == BSHelperDestinations.BEAT_SAVER_ROUTE,
            onClick = { if (currentRoute != BSHelperDestinations.BEAT_SAVER_ROUTE) navigateAction(BSHelperDestinations.BEAT_SAVER_ROUTE) },
            icon = { Icon(Icons.Filled.Web, stringResource(R.strings.beatsaver_title)) },
            label = { Text(stringResource(R.strings.beatsaver_title)) },
            alwaysShowLabel = false,
        )
        NavigationRailItem(
            selected = currentRoute == BSHelperDestinations.TOOLBOX_ROUTE,
            onClick = { if (currentRoute != BSHelperDestinations.TOOLBOX_ROUTE) navigateAction(BSHelperDestinations.TOOLBOX_ROUTE) },
            icon = { Icon(Icons.Filled.Settings, stringResource(R.strings.toolbox_title)) },
            label = { Text(stringResource(R.strings.toolbox_title)) },
            alwaysShowLabel = false,
        )
        Spacer(Modifier.weight(1f))
//        val list = listOf(GameType.LightBand, GameType.BeatSaberLike).map { it.human }
//        if(currentManageFolder != null) {
//            ChipDropDownSelector(
//                options = manageFolders.map { it.name },
//                selectedOption =  currentManageFolder.name,
//                onSelectedOptionChange = {str->
//                    manageFolders.find { it.name == str }?.let { onUIEvent(GlobalUIEvent.UpdateManageFolder(it)) }
//                }
//            )
//        }
    }
}

// @Preview("Drawer contents")
// @Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
// @Composable
// fun PreviewAppNavRail() {
//    LBHelperTheme {
//        AppNavRail(
//            currentRoute = LBHelperDestinations.HOME_ROUTE,
//            navigateToHome = {},
//            navigateToToolbox = {},
//            navigateToBSOnline = {}
//        )
//    }
// }
