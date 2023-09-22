package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.ui.route.BSHelperDestinations
import io.ktlab.bshelper.MR as R

@Composable
fun AppNavRail(
    currentRoute: String,
    navigateAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        header = {
            Icon(
                Icons.Default.Menu,
                null,
                Modifier.padding(vertical = 12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = modifier
    ) {
        Spacer(Modifier.weight(1f))
        NavigationRailItem(
            selected = currentRoute == BSHelperDestinations.HOME_ROUTE,
            onClick = { navigateAction(BSHelperDestinations.HOME_ROUTE) },
            icon = { Icon(Icons.Filled.Home, stringResource(R.strings.home_title)) },
            label = { Text(stringResource(R.strings.home_title)) },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == BSHelperDestinations.BEAT_SAVER_ROUTE,
            onClick = { navigateAction(BSHelperDestinations.BEAT_SAVER_ROUTE) },
            icon = { Icon(Icons.Filled.Web, stringResource(R.strings.beatsaver_title)) },
            label = { Text(stringResource(R.strings.beatsaver_title)) },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentRoute == BSHelperDestinations.TOOLBOX_ROUTE,
            onClick = { navigateAction(BSHelperDestinations.TOOLBOX_ROUTE) },
            icon = { Icon(Icons.Filled.Settings, stringResource(R.strings.toolbox_title)) },
            label = { Text(stringResource(R.strings.toolbox_title)) },
            alwaysShowLabel = false
        )
        Spacer(Modifier.weight(1f))
    }
}

//@Preview("Drawer contents")
//@Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//fun PreviewAppNavRail() {
//    LBHelperTheme {
//        AppNavRail(
//            currentRoute = LBHelperDestinations.HOME_ROUTE,
//            navigateToHome = {},
//            navigateToToolbox = {},
//            navigateToBSOnline = {}
//        )
//    }
//}