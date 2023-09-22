package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ktlab.bshelper.ui.route.BSHelperDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    currentRoute: String,
    navigateAction: (String) -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier) {
//        LBHelperLogo(
//            modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)
//        )
        NavigationDrawerItem(
            label = { Text("home") },
            icon = { Icon(Icons.Filled.Home, null) },
            selected = currentRoute == BSHelperDestinations.HOME_ROUTE,
            onClick = { navigateAction(BSHelperDestinations.HOME_ROUTE); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("beat saver") },
            icon = { Icon(Icons.Filled.Home, null) },
            selected = currentRoute == BSHelperDestinations.BEAT_SAVER_ROUTE,
            onClick = { navigateAction(BSHelperDestinations.BEAT_SAVER_ROUTE); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text("toolbox") },
            icon = { Icon(Icons.Filled.Settings, null) },
            selected = currentRoute == BSHelperDestinations.TOOLBOX_ROUTE,
            onClick = { navigateAction(BSHelperDestinations.TOOLBOX_ROUTE); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}