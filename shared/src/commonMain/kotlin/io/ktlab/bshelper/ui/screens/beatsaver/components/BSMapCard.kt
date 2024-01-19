package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.data.repository.LocalMapInfo
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.ui.components.MapAlertDialog
import io.ktlab.bshelper.ui.components.MapItemV2
import io.ktlab.bshelper.ui.components.MapItemV3
import io.ktlab.bshelper.ui.components.MapOnlinePreview
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.home.playlist.PlaylistCard
import io.ktlab.bshelper.ui.theme.BSHelperTheme
import io.ktlab.bshelper.ui.event.BeatSaverUIEvent

@Composable
fun BSMapCard(
    map: IMap,
    modifier: Modifier = Modifier,
    checked: Boolean,
    localInfo: LocalMapInfo? = null,
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
    Box (
        modifier = modifier
            .widthIn(min=350.dp)
            .padding(8.dp)
            .wrapContentSize()
    ){
        MapItemV3(
            modifier =Modifier,
            map = map,
            onLongClick = { onUIEvent(BeatSaverUIEvent.MapLongTapped(map)) },
            onClick = { onUIEvent(BeatSaverUIEvent.MapTapped(map)) },
            onAuthorClick = { if (enableAuthorClick) onUIEvent(BeatSaverUIEvent.OnSelectedBSMapper((map as BSMapVO).uploader.id)) },
            actionBar = {
                    var previewDialogOpen by remember { mutableStateOf(false) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (!multiSelectedMode) {
                            DownloadIconButton(
                                onClick = { onUIEvent(BeatSaverUIEvent.DownloadMap(map)) },
                                downloadInfo = downloadInfo,
                                modifier = Modifier.size(28.dp),
                            )
                        }
                        IconButton(
                            onClick = { onUIEvent(BeatSaverUIEvent.PlayPreviewMusicSegment(map)) },
                            modifier = Modifier.size(28.dp),
                        ) {
                            Icon(
                                Icons.Rounded.MusicNote,
                                contentDescription = "Play Preview Music Segment",
                            )
                        }
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
        )
        Column(
            Modifier
                .align(Alignment.BottomEnd)
        ) {
            if (multiSelectedMode) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { onMapMultiSelected(map) },
                    enabled = localInfo == null,
                    modifier =
                    Modifier
                        .size(28.dp)
                    ,
                )
            }
            localInfo?.let {
                Text(
                    text = localInfo.totalCount.toString(),
                    modifier = Modifier
                        .padding(4.dp)
                )
            }
        }

    }
}