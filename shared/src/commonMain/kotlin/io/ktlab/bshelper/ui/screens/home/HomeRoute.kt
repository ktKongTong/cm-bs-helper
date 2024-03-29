package io.ktlab.bshelper.ui.screens.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.ktlab.bshelper.ui.viewmodel.GlobalUiState
import io.ktlab.bshelper.ui.viewmodel.HomeViewModel
import moe.tlaster.precompose.koin.koinViewModel

@Composable
fun HomeRoute(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    snackbarHost: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState,
    globalUiState: GlobalUiState,
    homeViewModel: HomeViewModel = koinViewModel<HomeViewModel>(),
) {
    val uiState by homeViewModel.uiState.collectAsState()
    HomeScreen(
        uiState = uiState,
        showTopAppBar = isExpandedScreen,
        snackbarHost = snackbarHost,
    )
}

private enum class HomeScreenType {
    PlaylistWithPlaylistDetails,
    Playlist,
    PlaylistDetails,
    MapDetails,
}

// private fun getHomeScreenType(
//    isExpandedScreen: Boolean,
//    uiState: HomeUiState
// ): HomeScreenType = when (isExpandedScreen) {
//    false -> {
//        when (uiState) {
// //            is HomeUiState.Empty -> {
// //                HomeScreenType.PlaylistWithPlaylistDetails
// //            }
//            else -> {
//                HomeScreenType.PlaylistWithPlaylistDetails
//            }
//        }
//    }
//    true -> HomeScreenType.PlaylistWithPlaylistDetails
// }
