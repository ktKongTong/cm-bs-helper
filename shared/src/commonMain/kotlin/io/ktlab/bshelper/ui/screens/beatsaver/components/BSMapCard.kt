package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.ui.components.MapAlertDialog
import io.ktlab.bshelper.ui.components.MapItemV2
import io.ktlab.bshelper.ui.components.MapOnlinePreview
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.home.playlist.PlaylistCard
import io.ktlab.bshelper.ui.theme.BSHelperTheme
import io.ktlab.bshelper.ui.viewmodel.BeatSaverUIEvent

@Composable
fun BSMapCard(
    map: IMap,
    modifier: Modifier = Modifier,
    checked: Boolean,
    local: Boolean = false,
    selectedBSMap: IMap? = null,
    onMapMultiSelected: (IMap) -> Unit,
    downloadInfo: IDownloadTask.MapDownloadTask?,
    onUIEvent: (UIEvent) -> Unit,
    onDownloadMap: (IMap) -> Unit,
    multiSelectedMode: Boolean = false,
    selectableIPlaylists: List<IPlaylist>,
    onPlayPreviewMusicSegment: (IMap) -> Unit = {},
    enableAuthorClick: Boolean = true,
) {
    MapItemV2(
        modifier = modifier.widthIn(min = 350.dp).padding(8.dp),
        map = map,
        imageMaxWidth = 250.dp,
        onLongClick = { onUIEvent(BeatSaverUIEvent.MapLongTapped(map)) },
        onClick = { onUIEvent(BeatSaverUIEvent.MapTapped(map)) },
        onAuthorClick = { if (enableAuthorClick) onUIEvent(BeatSaverUIEvent.OnSelectedBSMapper((map as BSMapVO).uploader.id)) },
    ) {
        var previewDialogOpen by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                if (!multiSelectedMode) {
                    DownloadIconButton(
                        onClick = { onUIEvent(BeatSaverUIEvent.DownloadMap(map)) },
                        downloadInfo = downloadInfo,
                        modifier = Modifier.size(28.dp),
                        localExist = local,
                    )
                } else if (!local) {
                    Checkbox(
                        modifier = Modifier.size(28.dp),
                        checked = checked,
                        onCheckedChange = { onMapMultiSelected(map) },
                    )
                } else {
                    IconButton(
                        {},
                        enabled = false,
                        modifier = Modifier.size(28.dp),
                    ) {
                        Icon(Icons.Rounded.Check, contentDescription = "local icon")
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { onUIEvent(BeatSaverUIEvent.PlayPreviewMusicSegment(map)) },
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    Icons.Rounded.MusicNote,
                    contentDescription = "Play Preview Music Segment",
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { previewDialogOpen = true },
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    Icons.Rounded.PlayArrow,
                    contentDescription = "Play Map Preview",
                )
            }
        }
        if (previewDialogOpen) {
            MapOnlinePreview(onDismiss = { previewDialogOpen = false }, mapId = map.getID())
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
            if (targetPlaylist == null) {
//                Toast.makeText(context, "please select a target playlist", Toast.LENGTH_SHORT).show()
            } else {
                onConfirm(targetPlaylist!!)
                onDismiss()
            }
        },
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            verticalArrangement = Arrangement.Center,
            content = {
                if (targetPlaylists.isEmpty()) {
                    item {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(text = "no playlist")
                        }
                    }
                } else {
                    items(targetPlaylists.size) {
                        val playlist = targetPlaylists[it]
                        PlaylistCard(
                            playlist = playlist,
                            onClick = { _ ->
                                targetPlaylist = playlist
                            },
                            selected = (targetPlaylist?.id == playlist.id),
                        )
                    }
                }
            },
        )
    }
}

@Composable
fun BSMapCardPreview() {
    BSHelperTheme {
//        BSMapCard(
//            map = bsMapVO,
//            checked = false,
//            downloadInfo = null,
//            onDownloadMap = {_->},
//            onMapMultiSelected = {},
//            selectableIPlaylists = listOf(),
//            onUIEvent = {}
//        )
    }
}
