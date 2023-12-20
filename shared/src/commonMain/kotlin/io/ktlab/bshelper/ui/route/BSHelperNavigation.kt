package io.ktlab.bshelper.ui.route

import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator

object BSHelperDestinations {
    const val HOME_ROUTE = "home"
    const val BEAT_SAVER_ROUTE = "beat_saver"
    const val TOOLBOX_ROUTE = "toolbox"
}

class BSHelperNavigationActions(navigator: Navigator) {
    val navigateToHome: () -> Unit = {
        navigator.navigate(
            BSHelperDestinations.HOME_ROUTE,
            NavOptions(
//            popUpTo = navigator.,
                launchSingleTop = true,
                includePath = true,
            ),
        )
//        navigator.popUntilRoot()
//        navigator.push(HomeNavScreen)
//        navController.navigate(LBHelperDestinations.HOME_ROUTE) {
// //            popUpTo(navController.graph.findStartDestination().id) {
// //                saveState = true
// //            }
// //            launchSingleTop = true
// //            restoreState = true
//        }
    }

    val navigateToBeatSaver: () -> Unit = {
//        navigator.canGoBack.c
        navigator.navigate(
            BSHelperDestinations.BEAT_SAVER_ROUTE,
            NavOptions(
//            popUpTo = navigator.,
                launchSingleTop = true,
                includePath = true,
            ),
        )
//        navigator.popUntilRoot()
//        navigator.push(BSNavScreen)
//        navController.navigate(LBHelperDestinations.BS_ONLINE_ROUTE) {
// //            popUpTo(navController.graph.findStartDestination().id) {
// //                saveState = true
// //            }
// //            launchSingleTop = true
// //            restoreState = true
//        }
    }

    val navigateToToolbox: () -> Unit = {
        navigator.navigate(
            BSHelperDestinations.TOOLBOX_ROUTE,
            NavOptions(
//            popUpTo = navigator.,
                launchSingleTop = true,
                includePath = true,
            ),
        )
//        navigator.popUntilRoot()
//        navigator.push(ToolboxNavScreen)
//        navController.navigate(LBHelperDestinations.TOOLBOX_ROUTE) {
// //            popUpTo(navController.graph.findStartDestination().id) {
// //                saveState = true
// //            }
// //            launchSingleTop = true
// //            restoreState = true
//        }
    }
}
