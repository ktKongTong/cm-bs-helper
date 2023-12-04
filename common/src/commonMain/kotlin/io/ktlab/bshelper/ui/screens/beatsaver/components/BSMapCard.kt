package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.repository.IDownloadTask
import io.ktlab.bshelper.ui.components.*
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.home.playlist.PlaylistCard
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent
import io.ktlab.kown.model.DownloadListener
import io.ktlab.kown.model.DownloadTaskBO
import io.ktlab.kown.model.KownTaskStatus
import io.ktlab.kown.model.RenameStrategy
import io.ktlab.bshelper.model.enums.MapTag as MapTagEnum

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun BSMapCard(
    map: IMap,
    modifier: Modifier = Modifier,
    checked: Boolean,
    local: Boolean = false,
    onMapMultiSelected: (IMap) -> Unit,
    downloadInfo: IDownloadTask.MapDownloadTask?,
    onUIEvent: (UIEvent) -> Unit,
    onDownloadMap: (IMap) -> Unit,
    multiSelectedMode: Boolean = false,
    selectableIPlaylists : List<IPlaylist>,
    onPlayPreviewMusicSegment: (IMap) -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .widthIn(min = 350.dp)
            .fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .combinedClickable(
                    onLongClick = {onUIEvent(BeatSaverUIEvent.MapLongTapped(map))},
                    onClick = {onUIEvent(BeatSaverUIEvent.MapTapped(map))}
                )
        ) {

            AsyncImageWithFallback(
                modifier = Modifier
                    .padding(PaddingValues(start = 0.dp, top = 8.dp, end = 8.dp, bottom = 0.dp))
                    .size(128.dp, 128.dp)
                    .align(Alignment.Top)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .clickable { onPlayPreviewMusicSegment(map) },
                source = map.getAvatar(),
            )
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ){
                Text(text = map.getSongName(),
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                FlowRow {
                    if ((map as BSMapVO).uploader.verifiedMapper?.let { true } == true) {
                        Icon(
                            Icons.Filled.Verified,
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Verified Mapper",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    MapperIconWIthText(text = map.getAuthor())
                    Spacer(modifier = Modifier.width(8.dp))
                    DurationIconWIthText(text = map.getDuration())
                    Spacer(modifier = Modifier.width(8.dp))
                    NPSIconWIthText(text = "%.2f".format(map.getMaxNPS()))
                }
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    FlowRow {
                        MapTagEnum.sort((map as BSMapVO).map.tags).map {
                            MapTag(tag = it)
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        ThumbUpIconWIthText(text = (map as BSMapVO).map.upVotes.toString())
                        Spacer(modifier = Modifier.width(8.dp))
                        ThumbDownIconWIthText(text = map.map.downVotes.toString())
                    }
                    Spacer(modifier = Modifier.width(8.dp))
    //                Text(text = "difficulties", modifier = Modifier.padding(end = 4.dp), style = MaterialTheme.typography.bodyMedium)
                    MapDiffLabel(diff = map.getDiffMatrix())
                }


                Row {
                    if (!local) {
                        DownloadIconButton(
                            onClick = {onUIEvent(BeatSaverUIEvent.DownloadMap(map))},
                            downloadInfo = downloadInfo,
                        )
                    }else {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = "local icon",
//                            Modifier.size(32.dp)
                        )
                    }
//                    IconButton(onClick = {
//
//                    },modifier=Modifier.size(36.dp)){
//                        Icon(
//                            Icons.Filled.MusicOff,
//                            contentDescription = "Download Map",
//                            tint = MaterialTheme.colorScheme.surfaceTint
//                        )
//                    }

                    var previewDialogOpen by remember { mutableStateOf(false) }
                    if (previewDialogOpen) {
                        MapOnlinePreview(onDismiss = { previewDialogOpen = false }, mapId = map.getID())
                    }
                }
            }
            if (multiSelectedMode && !local) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { onMapMultiSelected(map) },
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }

}


@Composable
fun DownloadSelectPlaylistDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: (IPlaylist) -> Unit,
    targetPlaylists: List<IPlaylist>,
) {
//    val context = LocalContext.current
    var targetPlaylist by remember { mutableStateOf<IPlaylist?>(null) }
    MapAlertDialog(
        title = "选择目标歌单",
        modifier = modifier,
        onDismiss = onDismiss,
        onConfirm = {
            if (targetPlaylist == null){
//                Toast.makeText(context, "please select a target playlist", Toast.LENGTH_SHORT).show()
            }else {
                onConfirm(targetPlaylist!!)
                onDismiss()
            }
        }
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            verticalArrangement = Arrangement.Center,
            content = {
                if (targetPlaylists.isEmpty()){
                    item {
                        Row (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ){
                            Text(text = "no playlist")
                        }
                    }
                }else{
                    items(targetPlaylists.size){
                        val playlist = targetPlaylists[it]
                        PlaylistCard(
                            playlist = playlist,
                            onClick = {_ ->
                                targetPlaylist = playlist
                            },
                            selected = (targetPlaylist?.id == playlist.id)
                        )
                    }
                }
            })
    }
}




// fake a DownloadTaskBO
private val downloadTaskBO = DownloadTaskBO(
    taskId = "fake",
    title = "fake",
    tag = "fake",
    headers = mapOf(),
    status = KownTaskStatus.Running,
    url = "fake",
    eTag = "fake",
    dirPath = "fake",
    renameAble = false,
    renameStrategy = RenameStrategy.DEFAULT,
    filename = "fake",
    requestTimeout = 0,
    connectTimeout = 0,
    totalBytes = 33233,
    downloadedBytes = 333333,
    lastModifiedAt = 0,
    estimatedTime = 0,
    speed = 0,
    downloadListener = DownloadListener(),
    relateEntityId = "3f404"
)

//@Preview
//@Composable
//fun BSMapCardPreview(){
//    val downloadInfo = IDownloadTask.MapDownloadTask(
//        downloadTaskModel = downloadTaskBO,
//        relateMap = bsMapVO,
//
//    )
//    BSHelperTheme {
//        BSMapCard(
//            map = bsMapVO,
//            checked = false,
//            downloadInfo = downloadInfo,
//            onDownloadMap = {_->},
//            onMapMultiSelected = {},
//            selectableIPlaylists = listOf(),
//            onUIEvent = {}
//        )
//    }
//}