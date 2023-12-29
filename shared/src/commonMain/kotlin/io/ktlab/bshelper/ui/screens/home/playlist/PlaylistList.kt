package io.ktlab.bshelper.ui.screens.home.playlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.ui.LocalUIEventHandler
import io.ktlab.bshelper.ui.components.BSSearchBar
import io.ktlab.bshelper.ui.components.EmptyContent
import io.ktlab.bshelper.ui.components.FSPlaylistFormV2
import io.ktlab.bshelper.ui.components.FSPlaylistImportFormV2
import io.ktlab.bshelper.ui.event.GlobalUIEvent
import io.ktlab.bshelper.ui.event.HomeUIEvent
import io.ktlab.bshelper.ui.screens.beatsaver.components.IconExposedDropDownMenu

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    playlists: List<IPlaylist>,
    selectedPlaylist: IPlaylist?,
    stickyHeader: @Composable () -> Unit = {},
) {
    val onUIEvent = LocalUIEventHandler.current
    var query by remember { mutableStateOf("") }
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize(),
        contentPadding = contentPadding,
        state = state,
    ) {
        stickyHeader {
            Surface {
                Row(
                    Modifier.padding(vertical = 4.dp, horizontal = 16.dp).fillParentMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BSSearchBar(
                        Modifier.weight(1f, fill = false),
                        query = query,
                        onQueryChange = { query = it },
                        onClear = { query = "" },
                    )
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val playlistFormOpenState = remember { mutableStateOf(false) }
                        val playlistImportFormOpenState = remember { mutableStateOf(false) }
                        IconExposedDropDownMenu {
                            DropdownMenuItem(
                                text = { Text(text = "导入") },
                                onClick = {
                                    onTrigger()
                                    playlistImportFormOpenState.value = true
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(text = "新增") },
                                onClick = {
                                    onTrigger()
                                    playlistFormOpenState.value = true
                                },
                            )
                        }
                        FSPlaylistImportFormV2(
                            onUIEvent = onUIEvent,
                            selectablePlaylists = playlists,
                            openState = playlistImportFormOpenState,
                        )
                        FSPlaylistFormV2(
                            openState = playlistFormOpenState,
                            onSubmitFSPlaylist = { onUIEvent(GlobalUIEvent.CreatePlaylist(it)) },
                            checkIfExist = {
                                playlists.any { playlist -> playlist.getName() == it }
                            },
                        )
                    }
                }
            }
        }
        val selectablePlaylists =
            playlists.filter {
                it.id.contains(query) ||
                    it.getName().contains(query) || selectedPlaylist?.id == it.id
            }
        if (selectablePlaylists.isNotEmpty()) {
            items(selectablePlaylists.size) {
                PlaylistCard(
                    playlist = selectablePlaylists[it],
                    onClick = { playlistId ->
                        onUIEvent(HomeUIEvent.PlaylistTapped(playlistId))
                    },
                    selected = selectedPlaylist?.id == selectablePlaylists[it].id,
                    onUIEvent = onUIEvent,
                )
            }
        } else {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                ) {
                    EmptyContent()
                }
            }
        }
    }
}
