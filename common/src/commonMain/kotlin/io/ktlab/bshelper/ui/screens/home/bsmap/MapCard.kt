package io.ktlab.bshelper.ui.screens.home.bsmap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.fakeFSMapVO
import io.ktlab.bshelper.ui.components.MapItemV2
import io.ktlab.bshelper.ui.components.MapOnlinePreview
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.theme.BSHelperTheme
import io.ktlab.bshelper.viewmodel.HomeUIEvent


@Composable
fun MapCard(
    map: IMap,
    modifier: Modifier = Modifier,
    checked: Boolean,
    multiSelectedMode: Boolean = false,
    onUIEvent: (UIEvent) -> Unit,
) {
    MapItemV2(
        map = map,
        onClick = { onUIEvent(HomeUIEvent.MapTapped(map.getID())) },
        onLongClick = {},
        onAvatarClick = {},
        modifier = modifier
            .fillMaxWidth(),
        onAuthorClick = {},
        menuArea = {
            if (multiSelectedMode) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { onUIEvent(HomeUIEvent.MapMultiSelected(map)) },
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.Center)
                )
            }else {
                var previewDialogOpen by remember { mutableStateOf(false) }
//                MapCardMenu(
//                    modifier = Modifier
//                        .size(28.dp)
//                        .align(Alignment.Center),
//                    onDelete = {
//                        onUIEvent(HomeUIEvent.MultiDeleteAction(setOf(map)))
//                    },
//                    onMove = {
//
//                    },
//                    onPreview = { previewDialogOpen = true },
//                )
                Row (
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ){
                   IconButton(onClick = { onUIEvent(HomeUIEvent.MultiDeleteAction(setOf(map))) }, modifier = Modifier.size(28.dp)) {
                       Icon(Icons.Default.Delete, contentDescription = "delete map")
                   }
                    IconButton(onClick = { onUIEvent(HomeUIEvent.PlayPreviewMusicSegment(map)) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.MusicNote, contentDescription = "play preview map")
                    }
                    IconButton(onClick = { previewDialogOpen = true }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "preview map")
                    }
                }
                if (previewDialogOpen) {
                    MapOnlinePreview(onDismiss = { previewDialogOpen = false }, mapId = map.getID())
                }
            }
        }
    )
}

@Composable
fun PreviewMapCard() {
    BSHelperTheme {
        println(fakeFSMapVO.getAvatar())
        Text(text = fakeFSMapVO.getAvatar())
//        Text(text = "PreviewMapCard")
        MapCard(map = fakeFSMapVO, checked = false, multiSelectedMode = false, onUIEvent = {})
    }
}