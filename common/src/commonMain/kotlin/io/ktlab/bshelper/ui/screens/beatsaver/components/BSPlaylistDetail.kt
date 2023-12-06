package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import app.cash.paging.compose.collectAsLazyPagingItems
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.vo.BSPlaylistVO
import io.ktlab.bshelper.repository.IDownloadTask
import io.ktlab.bshelper.ui.components.AsyncImageWithFallback
import io.ktlab.bshelper.ui.components.DropDownPlaylistSelector
import io.ktlab.bshelper.ui.components.MapperLabel
import io.ktlab.bshelper.ui.components.labels.BSDurationLabel
import io.ktlab.bshelper.ui.components.labels.BSNPSRangeLabel
import io.ktlab.bshelper.ui.components.labels.BSThumbDownLabel
import io.ktlab.bshelper.ui.components.labels.BSThumbUpLabel
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.utils.prettyFormat
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverUiState
import io.ktlab.bshelper.viewmodel.LocalState
import kotlinx.coroutines.flow.Flow

@Composable
fun BSPlaylistDetail(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyGridState = rememberLazyGridState(),
    snackbarHostState: SnackbarHostState,
    localState: LocalState,
    playlist: IPlaylist,
    uiState: BeatSaverUiState,
    onUIEvent: (UIEvent) -> Unit,
    mapFlow: Flow<PagingData<IMap>>,
){
    uiState as BeatSaverUiState.PlaylistQuery
    val mapPagingItems = uiState.mapFlow.collectAsLazyPagingItems()
    val downloadingTasks = uiState.downloadTaskFlow.collectAsState(initial = emptyList()).value.flatMap {
        when(it) {
            is IDownloadTask.MapDownloadTask -> listOf(it)
            is IDownloadTask.BatchDownloadTask -> it.taskList
            is IDownloadTask.PlaylistDownloadTask -> it.taskList
        }
    }.associateBy { it.downloadTaskModel.relateEntityId!! }
    Row {
        MapCardPagingList(
            Modifier,
            snackbarHostState = snackbarHostState,
            localState = localState,
            mapMultiSelectedMode = uiState.multiSelectMode,
            mapMultiSelected = uiState.multiSelectedBSMap,
            mapPagingItems = mapPagingItems,
            downloadingTask = downloadingTasks,
            onUIEvent = onUIEvent,
            stickyHeader = {
                BSPlaylistHeader(
                    playlist = playlist,
                    uiState = uiState,
                    onUIEvent = onUIEvent,
                    count = 0,
                    multiSelectedMode = uiState.multiSelectMode,
                    multiSelectedBSMap = uiState.multiSelectedBSMap,
                    mapFlow = mapFlow,
                    localState = localState,
                )
            }
        )
    }
}

@Composable
fun BSPlaylistHeader(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    localState: LocalState,
    playlist: IPlaylist,
    uiState: BeatSaverUiState,
    onUIEvent: (UIEvent) -> Unit,
    count: Int,
    multiSelectedMode: Boolean,
    multiSelectedBSMap: Set<IMap>,
    mapFlow: Flow<PagingData<IMap>>,
){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column {
            Row {
                TextButton(
                    onClick = { onUIEvent(BeatSaverUIEvent.OnExitSelectBSPlaylist) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(text = "Back")
                }
                Text(text = playlist.getName(), style = MaterialTheme.typography.titleLarge)
            }

            Row {
                AsyncImageWithFallback(
                    modifier = Modifier
                        .padding(PaddingValues(start = 0.dp, top = 8.dp, end = 8.dp, bottom = 8.dp))
                        .size(128.dp, 128.dp)
                        .align(Alignment.Top)
                        .clip(shape = RoundedCornerShape(10.dp)),
                    source = playlist.getAvatar(),
                )

                Column(
                    modifier = Modifier
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MapperLabel(
                        mapperName = playlist.getAuthor(),
                        onClick = {},
                        verified = (playlist as BSPlaylistVO).owner.verifiedMapper?.let { true } == true,
                        avatarUrl = (playlist as BSPlaylistVO).owner.avatar
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BSNPSRangeLabel(npsRange = "%.2f - %.2f".format(playlist.getMinNPS(), playlist.getMaxNPS()))
                        Spacer(modifier = Modifier.width(8.dp))
                        BSDurationLabel(duration = playlist.getTotalDuration().toString())
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BSThumbUpLabel(playlist.playlist.upVotes)
                        Spacer(modifier = Modifier.width(8.dp))
                        BSThumbDownLabel(playlist.playlist.downVotes)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Maps: $count",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Updated: ${playlist.playlist.createdAt.prettyFormat()}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }

            }

            Row {
                DropDownPlaylistSelector(
                    onUIEvent = onUIEvent,
                    modifier = Modifier,
                    selectablePlaylists = localState.selectableLocalPlaylists,
                    selectedIPlaylist = localState.targetPlaylist,
                    onSelectedPlaylist = {
                        onUIEvent(BeatSaverUIEvent.ChangeTargetPlaylist(it))
                    },
                )
            }
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (multiSelectedMode) Arrangement.SpaceBetween else Arrangement.End
            ){
                if (multiSelectedMode) {
                    Text(text = "已选中: ${multiSelectedBSMap.size}", modifier = Modifier.align(Alignment.CenterVertically))
                    TextButton(onClick = {
                        if (localState.targetPlaylist != null) {
                            onUIEvent(BeatSaverUIEvent.MultiDownload(localState.targetPlaylist))
                        }else {
                            onUIEvent(BeatSaverUIEvent.ShowSnackBar("请选择目标歌单"))
                        }
                    }) {
                        Text(text = "Download")
                    }
                }
                Box(
                ){
                    IconButton(onClick = {
                        onUIEvent(BeatSaverUIEvent.ChangeMultiSelectMode(!multiSelectedMode))
                    }, modifier = Modifier) {
                        if (!multiSelectedMode) {
                            Icon(Icons.Default.QueueMusic, contentDescription = stringResource(MR.strings.multi_select))
                        }else {
                            Icon(Icons.Default.Cancel, contentDescription = stringResource(MR.strings.cancel_multi_select))
                        }
                    }
                }

            }
        }
    }
}