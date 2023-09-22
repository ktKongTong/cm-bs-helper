package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.ui.components.AsyncImageWithFallback
import io.ktlab.bshelper.ui.components.BPMIconWIthText
import io.ktlab.bshelper.ui.components.DurationIconWIthText
import io.ktlab.bshelper.ui.components.MapAlertDialog
import io.ktlab.bshelper.ui.components.MapDiffLabel
import io.ktlab.bshelper.ui.components.MapOnlinePreview
import io.ktlab.bshelper.ui.components.MapperIconWIthText
import io.ktlab.bshelper.ui.components.NPSIconWIthText
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.home.playlist.PlaylistCard
import io.ktlab.bshelper.ui.theme.BSHelperTheme

data class DownloadTask(
    val id:String,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BSMapCard(
    map: IMap,
    modifier: Modifier = Modifier,
    checked: Boolean,
    local: Boolean = false,
    onMapMultiSelected: (IMap) -> Unit,
    downloadInfo: DownloadTask?,
    onUIEvent: (UIEvent) -> Unit,
    onDownloadMap: (IMap,String,String) -> Unit,
    multiSelectedMode: Boolean = false,
    selectableIPlaylists : List<IPlaylist>,
    onPlayPreviewMusicSegment: (IMap) -> Unit = {},
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
        ) {
            AsyncImageWithFallback(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .size(64.dp, 64.dp)
                    .align(Alignment.CenterVertically)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .clickable { onPlayPreviewMusicSegment(map) },
                source = map.getAvatar(),
            )
            Column (
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
    //                Text(text = map.getAuthor(), modifier = Modifier.padding(bottom = 4.dp), style = MaterialTheme.typography.bodyMedium)
                MapperIconWIthText(text = map.getAuthor())
                MapDiffLabel(diff = map.getDiffMatrix())
            }
            Column(
                modifier = Modifier
                    .width(120.dp)
                    .padding(end = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                DurationIconWIthText(text = map.getDuration())
                BPMIconWIthText(text = map.getBPM())
                NPSIconWIthText(text = "%.2f".format(map.getMaxNPS()))
            }
            if (multiSelectedMode && !local) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { onMapMultiSelected(map) },
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .align(Alignment.CenterVertically)
                )
            }else {
//                val context = LocalContext.current
                Column {
                    if (local){
                        IconButton(onClick = {
//                            Toast
//                                .makeText(
//                                    context, "already exist!😉",
//                                    Toast.LENGTH_SHORT
//                                )
//                                .show()
                        }) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = "Downloaded Map",
                                tint = Color.Green
                            )
                        }
                    }else {
                        if (downloadInfo == null) {
                            var selectPath by remember { mutableStateOf("") }
                            var targetPathSelectDialogOpen by remember { mutableStateOf(false) }
                            IconButton(onClick = { targetPathSelectDialogOpen = true }) {
                                Icon(
                                    Icons.Filled.Download,
                                    contentDescription = "Download Map",
                                )
                            }
                            if (targetPathSelectDialogOpen) {
                                DownloadSelectPlaylistDialog(
                                    onDismiss = { targetPathSelectDialogOpen = false },
                                    onConfirm = { selectPath = it.getTargetPath();onDownloadMap(map,selectPath,it.id) },
                                    targetPlaylists = selectableIPlaylists
                                )
                            }
                        }else {
//                            IconButton(
//                                onClick = {
////                                    if (downloadInfo.status == DownloadStatus.PAUSED) {
////                                        onUIEvent(BeatSaverUIEvent.ResumeDownload(downloadInfo))
////                                    }else {
////                                        onUIEvent(BeatSaverUIEvent.PauseDownload(downloadInfo))
////                                    }
//                                }
//                                ) {
//                                when(downloadInfo.status) {
//                                    DownloadStatus.DOWNLOADING -> {
//                                        Icon(
//                                            Icons.Filled.Downloading,
//                                            contentDescription = "Downloading Map",
//                                            tint = Color.Blue
//                                        )
//                                    }
//                                    DownloadStatus.PAUSED -> {
//                                        Icon(
//                                            Icons.Filled.Pause,
//                                            contentDescription = "Paused Map",
//                                            tint = Color.Gray
//                                        )
//                                    }
//                                    DownloadStatus.CANCELED -> {
//                                        Icon(
//                                            Icons.Filled.Cancel,
//                                            contentDescription = "Paused Map",
//                                            tint = Color.Gray
//                                        )
//                                    }
//                                    else -> {}
//                                }
//                            }
                        }

                    }
                    var previewDialogOpen by remember { mutableStateOf(false) }
                    IconButton(onClick = { previewDialogOpen = true }) {
                        Icon(
                            Icons.Default.Preview,
                            contentDescription = "Preview Map"
                        )
                    }
                    if (previewDialogOpen) {
                        MapOnlinePreview(onDismiss = { previewDialogOpen = false }, mapId = map.getID())
                    }
                }
            }
        }
//        if(downloadInfo != null && (downloadInfo.status != DownloadStatus.FINISHED && downloadInfo.status != DownloadStatus.FAILED)) {
//            Row {
//                LinearProgressIndicator(progress = downloadInfo.progress, modifier = Modifier.weight(1f))
//            }
//        }
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


//@Preview
//@Composable
//fun BSMapCardPreview(){
////    val downloadInfo = DownloadTask.MapDownloadTask(
////        id = "test",
////        progress = 0.79f,
////        startTime = 0L,
////        endTime = 0L,
////        status = DownloadStatus.DOWNLOADING,
////        total = 100,
////        speed = 100,
////        mapId = "test",
////        mapName = "test",
////        relateRequest = DownloadRequest.MapDownloadRequest(
////            title = "test",
////            id = "test",
////            downloadProgressListener = { _ -> },
////            onSuccessCallback = { },
////            url = "",
////            targetPath = "",
////            bsMap = bsMapViewExample
////        )
////    )
//
//    BSHelperTheme {
////        BSMapCard(
////            map = bsMapViewExample,
////            checked = false,
////            downloadInfo = downloadInfo,
////            onDownloadMap = {_,_,_->},
////            onMapMultiSelected = {},
////            selectableIPlaylists = listOf(),
////        )
//    }
//}