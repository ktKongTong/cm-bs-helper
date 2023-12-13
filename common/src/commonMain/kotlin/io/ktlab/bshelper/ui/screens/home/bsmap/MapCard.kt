package io.ktlab.bshelper.ui.screens.home.bsmap

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.fakeFSMapVO
import io.ktlab.bshelper.ui.components.MapItem
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
    MapItem(
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
                        .padding(end = 16.dp)
                        .align(Alignment.Center)
                )
            }else {
                var previewDialogOpen by remember { mutableStateOf(false) }
                MapCardMenu(
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .align(Alignment.Center),
                    onDelete = {
                        onUIEvent(HomeUIEvent.MultiDeleteAction(setOf(map)))
                    },
                    onMove = {

                    },
                    onPreview = { previewDialogOpen = true },
                )
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