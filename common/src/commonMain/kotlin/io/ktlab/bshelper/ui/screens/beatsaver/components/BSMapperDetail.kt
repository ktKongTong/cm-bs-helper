package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import app.cash.paging.compose.collectAsLazyPagingItems
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.model.dto.response.BSMapperDetailDTO
import io.ktlab.bshelper.ui.components.AsyncImageWithFallback
import io.ktlab.bshelper.ui.components.DropDownPlaylistSelector
import io.ktlab.bshelper.ui.components.labels.BSThumbDownLabel
import io.ktlab.bshelper.ui.components.labels.BSThumbUpLabel
import io.ktlab.bshelper.ui.components.labels.MapAmountLabel
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverUiState
import io.ktlab.bshelper.viewmodel.GlobalUIEvent
import io.ktlab.bshelper.viewmodel.LocalState
import kotlinx.coroutines.flow.Flow

@Composable
fun BSMapperDetailOverview(
    selectedBSMapper: BSMapperDetailDTO,
){
    Column (
        modifier = Modifier
            .width(300.dp)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ){
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
        ){
            AsyncImageWithFallback(
                source = selectedBSMapper.avatar,
                modifier = Modifier
                    .clickable(onClick = {})
                    .clip(shape =  RoundedCornerShape(16.dp))
                    .background(Color.Blue)
                ,
                contentScale = ContentScale.FillWidth,
            )
        }
        Text("Stats", style = MaterialTheme.typography.headlineMedium)
        MapAmountLabel(selectedBSMapper.stats.totalMaps, textStyle = MaterialTheme.typography.labelLarge)
        Row {
            BSThumbUpLabel(selectedBSMapper.stats.totalUpvotes.toLong(), textStyle = MaterialTheme.typography.labelLarge)
            BSThumbDownLabel(selectedBSMapper.stats.totalDownvotes.toLong(), textStyle = MaterialTheme.typography.labelLarge)
        }
        Text("Description", style = MaterialTheme.typography.headlineMedium)
        Text(selectedBSMapper.description.ifEmpty { "mapper 在这里什么都没说..." })
    }
}

@Composable
fun BSMapperDetail(
    uiState: BeatSaverUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyGridState = rememberLazyGridState(),
    snackbarHostState: SnackbarHostState,
    localState: LocalState,
    onUIEvent: (UIEvent) -> Unit,
    mapFlow: Flow<PagingData<IMap>>,
) {
    uiState as BeatSaverUiState.MapperQuery
    val mapPagingItems = uiState.mapFlow.collectAsLazyPagingItems()
    val downloadingTasks = uiState.downloadTaskFlow.collectAsState(initial = emptyList()).value.flatMap {
        when(it) {
            is IDownloadTask.MapDownloadTask -> listOf(it)
            is IDownloadTask.BatchDownloadTask -> it.taskList
            is IDownloadTask.PlaylistDownloadTask -> it.taskList
        }
    }.associateBy { it.downloadTaskModel.relateEntityId!! }
    Row {
        BSMapperDetailOverview(selectedBSMapper = uiState.selectedBSMapper!!)
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
                Surface {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = { onUIEvent(BeatSaverUIEvent.OnExitBSMapper) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(
                                Icons.Rounded.ArrowBack,
                                contentDescription = "back icon"
                            )
                        }
                        Column(
                        ) {
                            DropDownPlaylistSelector(
                                onUIEvent = onUIEvent,
                                modifier = Modifier,
                                selectablePlaylists = localState.selectableLocalPlaylists,
                                selectedIPlaylist = localState.targetPlaylist,
                                onSelectedPlaylist = {
                                    onUIEvent(BeatSaverUIEvent.ChangeTargetPlaylist(it))
                                },
                            )
                            val multiSelectedMode = uiState.multiSelectMode
                            val multiSelectedBSMap = uiState.multiSelectedBSMap
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (multiSelectedMode) Arrangement.SpaceBetween else Arrangement.End
                            ) {
                                if (multiSelectedMode) {
                                    Text(
                                        text = "已选中: ${multiSelectedBSMap.size}",
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                                Row {
                                    if (multiSelectedMode) {
                                        TextButton(onClick = {
                                            if (localState.targetPlaylist != null) {
                                                onUIEvent(BeatSaverUIEvent.MultiDownload(localState.targetPlaylist))
                                            } else {
                                                onUIEvent(GlobalUIEvent.ShowSnackBar("请选择目标歌单"))
                                            }
                                        }) {
                                            Text(text = "Download")
                                        }
                                    }
                                    IconButton(onClick = {
                                        onUIEvent(BeatSaverUIEvent.ChangeMultiSelectMode(!multiSelectedMode))
                                    }, modifier = Modifier) {
                                        if (!multiSelectedMode) {
                                            Icon(
                                                Icons.Rounded.QueueMusic,
                                                contentDescription = stringResource(MR.strings.multi_select)
                                            )
                                        } else {
                                            Icon(
                                                Icons.Rounded.Cancel,
                                                contentDescription = stringResource(MR.strings.cancel_multi_select)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}