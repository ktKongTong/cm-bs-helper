package io.ktlab.bshelper.ui.screens.home.playlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.ui.components.EmptyContent
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.HomeUIEvent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    playlists: List<IPlaylist>,
    selectedPlaylist: IPlaylist?,
    onUIEvent: (UIEvent) -> Unit,
    stickyHeader : @Composable () -> Unit = {},
) {
    LazyColumn(
    modifier = modifier
        .fillMaxSize(),
    contentPadding = contentPadding,
    state = state,
    ) {
        stickyHeader {
            Surface(Modifier.fillParentMaxWidth()) {
                stickyHeader()
            }
        }
        if (playlists.isNotEmpty()) {
            items(playlists.size){
                PlaylistCard(
                    playlist = playlists[it],
                    onClick = {playlistId->
                              onUIEvent(HomeUIEvent.PlaylistTapped(playlistId))
                              },
                    selected = selectedPlaylist?.id == playlists[it].id,
                    onUIEvent = onUIEvent
                )
            }
        }else {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize()
                ) {
                    EmptyContent()
                }
            }
        }
    }
}
