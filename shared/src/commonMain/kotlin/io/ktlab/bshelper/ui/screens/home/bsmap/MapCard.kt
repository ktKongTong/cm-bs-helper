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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.ui.components.MapItemV2
import io.ktlab.bshelper.ui.components.MapOnlinePreview
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.theme.BSHelperTheme
import io.ktlab.bshelper.ui.event.HomeUIEvent

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
        modifier =
            modifier
                .fillMaxWidth(),
        imageMaxWidth = 350.dp,
        onAuthorClick = {},
        menuArea = {
            if (multiSelectedMode) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { onUIEvent(HomeUIEvent.MapMultiSelected(map)) },
                    modifier =
                        Modifier
                            .size(28.dp)
                            .align(Alignment.Center),
                )
            } else {
                var previewDialogOpen by remember { mutableStateOf(false) }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
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
        },
    )
}

@Composable
fun PreviewMapCard() {
    BSHelperTheme {
        println(io.ktlab.bshelper.model.fakeFSMapVO.getAvatar())
        Text(text = io.ktlab.bshelper.model.fakeFSMapVO.getAvatar())
//        Text(text = "PreviewMapCard")
        MapCard(map = io.ktlab.bshelper.model.fakeFSMapVO, checked = false, multiSelectedMode = false, onUIEvent = {})
    }
}
