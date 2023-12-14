package io.ktlab.bshelper.ui.route

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.ktlab.bshelper.ui.screens.home.HomeScreen
import io.ktlab.bshelper.viewmodel.GlobalUiState
import io.ktlab.bshelper.viewmodel.HomeViewModel
import moe.tlaster.precompose.koin.koinViewModel

@Composable
fun HomeRoute(
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    snackbarHost : @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState,
    globalUiState: GlobalUiState,
    homeViewModel:HomeViewModel = koinViewModel<HomeViewModel>()
){
    val uiState by homeViewModel.uiState.collectAsState()
//    val uiState = homeViewModel.uiState.collectAsState(homeViewModel.viewModelState.value.toUiState()).value
//    val homeListLazyListState = rememberLazyListState()
//    val playlistDetailLazyListStates = when (uiState) {
////        is HomeUiState.Empty ->  emptyList<Pair<String,String>>()
//        else ->emptyList<Pair<String,String>>()
//    }.associate { playlist ->
//        key(playlist.first) {
//            playlist.first to rememberLazyListState()
//        }
//    }
//    when (getHomeScreenType(isExpandedScreen, uiState)) {
//        HomeScreenType.PlaylistWithPlaylistDetails -> {
            HomeScreen(
                uiState = uiState,
                showTopAppBar = isExpandedScreen,
                onUIEvent = homeViewModel::dispatchUiEvents,
                snackbarHost = snackbarHost
            )
//        }
//        else -> {}
//    }
}

private enum class HomeScreenType {
    PlaylistWithPlaylistDetails,
    Playlist,
    PlaylistDetails,
    MapDetails
}

//private fun getHomeScreenType(
//    isExpandedScreen: Boolean,
//    uiState: HomeUiState
//): HomeScreenType = when (isExpandedScreen) {
//    false -> {
//        when (uiState) {
////            is HomeUiState.Empty -> {
////                HomeScreenType.PlaylistWithPlaylistDetails
////            }
//            else -> {
//                HomeScreenType.PlaylistWithPlaylistDetails
//            }
//        }
//    }
//    true -> HomeScreenType.PlaylistWithPlaylistDetails
//}