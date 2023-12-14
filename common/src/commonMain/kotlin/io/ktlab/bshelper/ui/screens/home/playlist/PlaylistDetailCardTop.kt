package io.ktlab.bshelper.ui.screens.home.playlist


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.ui.components.AsyncImageWithFallback
import io.ktlab.bshelper.ui.components.MapAlertDialog
import io.ktlab.bshelper.ui.components.SortButton
import io.ktlab.bshelper.ui.components.labels.BSNPSRangeLabel
import io.ktlab.bshelper.ui.components.labels.MapAmountLabel
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.HomeUIEvent
import io.ktlab.bshelper.viewmodel.MapListState

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailCardTop(
    playlist: IPlaylist,
    modifier: Modifier = Modifier,
    selectablePlaylists: List<IPlaylist> = listOf(),
    mapListState: MapListState,
    mapList: List<IMap>,
    onUIEvent: (UIEvent) -> Unit,
){
    val selectedMapHashMap = mapListState.multiSelectedMapHashMap
    val sortRule = mapListState.sortRule
    val selectedMapSize = selectedMapHashMap.size
    val multiSelectMode = mapListState.isMapMultiSelectMode

    val imageModifier = Modifier
        .padding(top = 2.dp, end = 16.dp)
        .clip(shape = RoundedCornerShape(10.dp))
        .size(64.dp, 64.dp)
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Row(
               modifier = Modifier
            ) {
                AsyncImageWithFallback(
                    source = playlist.getImage(),
                    contentDescription = "playlist image",
                    modifier = imageModifier,
                )
                Column {
                    Text(
                        text = playlist.title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    )
                    Text(
                        text = if (playlist.getAuthor()!="") {playlist.getAuthor()} else {"custom"},
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    )
                }
            }
            Column(
                Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                    BSNPSRangeLabel("${playlist.getMinNPS()} - ${playlist.getMaxNPS()}")
                    MapAmountLabel(playlist.getMapAmount())
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = playlist.getPlaylistDescription(),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
            )
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { }, modifier = Modifier) {
                    Row {
                        Icon(Icons.Default.FilterAlt, contentDescription = stringResource(MR.strings.filter))
                        Text(text = stringResource(MR.strings.filter),
                            style = MaterialTheme.typography.titleMedium)
                    }
                }
                SortButton(
                    sortRule = sortRule,
                    onChangeMapListSortRule = { onUIEvent(HomeUIEvent.ChangeMapListSortRule(it)) }
                )
                IconButton(onClick = {
                    onUIEvent(HomeUIEvent.ChangeMultiSelectMode(!multiSelectMode))
                }, modifier = Modifier) {
                    if (!multiSelectMode) {
                        Icon(Icons.Default.QueueMusic, contentDescription = stringResource(MR.strings.multi_select))
                    }else {
                        Icon(Icons.Default.Cancel, contentDescription = stringResource(MR.strings.cancel_multi_select))
                    }
                }
            }
            if(multiSelectMode){
                val contentPadding = PaddingValues(4.dp)
                var deleteDialogOpen by remember { mutableStateOf(false) }
                var moveDialogOpen by remember { mutableStateOf(false) }
//                val context = LocalContext.current
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val textButtonModifier = Modifier
                        .align(Alignment.CenterVertically)
                        .height(36.dp)
                    Text(
                        text = "选中 $selectedMapSize/${mapList.size}",
                        modifier = Modifier.align(Alignment.CenterVertically),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    TextButton(
                        onClick = {onUIEvent(HomeUIEvent.MultiMapFullChecked(mapList.associateBy { it.getID()}))},
                        modifier = textButtonModifier,
                        contentPadding = contentPadding
                    ) { Text(text = "全选") }
                    TextButton(
                        onClick = {onUIEvent(HomeUIEvent.MultiMapOppoChecked(
                            mapList
                                .filter { !selectedMapHashMap.containsKey(it.getID()) }
                                .associateBy { it.getID()}
                        ))},
                        modifier = textButtonModifier,
                        contentPadding = contentPadding
                    ) { Text(text = "反选") }
                    TextButton(
                        onClick = {
                            if (selectedMapSize<=0) {
//                                onUIEvent(HomeUIEvent.ChangeMultiSelectMode(false))
//                                Toast.makeText(context, "Please select a map at least!🙄", Toast.LENGTH_SHORT)
//                                    .show()
                            }else {
                                deleteDialogOpen = true
                            }
                        },
                        modifier = textButtonModifier,
                        contentPadding = contentPadding
                    ) {
                        Text(text = "删除")
                        if (deleteDialogOpen){
                            MapAlertDialog(
                                title = "确认删除以下 $selectedMapSize 个谱面？",
                                modifier = modifier,
                                onDismiss = { deleteDialogOpen = false },
                                onConfirm = {
//                                    Toast.makeText(context, "😲 Ops!  Not yet implemented!", Toast.LENGTH_SHORT).show()
                                    // TODO delete and observe progress
                                    deleteDialogOpen = false
                                    onUIEvent(HomeUIEvent.MultiDeleteAction(selectedMapHashMap.values.toSet()))
                                }
                            ){
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    content = {
                                        selectedMapHashMap.values.forEach {
                                        item {
                                            Text(text = it.getSongName())
                                        }
                                    }
                                })
                            }
                        }
                    }
                    TextButton(
                        onClick = {
                            if (selectedMapSize<=0) {
//                                Toast.makeText(context, "Please select a map at least!🙄", Toast.LENGTH_SHORT)
//                                    .show()
                            }else {
                                moveDialogOpen = true
                            }
                        },
                        modifier = textButtonModifier,
                        contentPadding = contentPadding
                    ) {
                        Text(text = "移动")
                        if (moveDialogOpen){
                            var targetPlaylist by remember { mutableStateOf<IPlaylist?>(null) }
                            MapAlertDialog(
                                title = "选择要移动的目标歌单",
                                modifier = modifier,
                                onDismiss = { moveDialogOpen = false;targetPlaylist = null },
                                onConfirm = {
                                    if (targetPlaylist == null){
//                                        Toast.makeText(context, "please select a target playlist", Toast.LENGTH_SHORT).show()
                                    }else {
//                                        Toast.makeText(context, "😲 Ops!  Not yet implemented!", Toast.LENGTH_SHORT).show()
                                        // TODO move and observe progress
                                        moveDialogOpen = false
                                        onUIEvent(HomeUIEvent.MultiMoveAction(selectedMapHashMap.values.toSet(), targetPlaylist!!))
                                    }
                                }
                            ){
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                ) {
                                    items(selectablePlaylists.size){
                                        PlaylistCard(
                                            playlist = selectablePlaylists[it],
                                            onClick = { _ -> targetPlaylist = selectablePlaylists[it] },
                                            selected = (targetPlaylist?.id == selectablePlaylists[it].id)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}




//@Preview
//@Composable
//fun PlaylistDetailCardTopPreview() {
//    BSHelperTheme {
//        Surface {
////            PlaylistDetailCardTop(playlistViewExample, multiSelectMode = false ,onMultiSelectChecked = { } ,onChangeMapListSortRule = { })
//        }
//    }
//}
