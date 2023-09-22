package io.ktlab.bshelper.ui.screens.home.bsmap

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.ui.components.AsyncImageWithFallback
import io.ktlab.bshelper.ui.components.BPMIconWIthText
import io.ktlab.bshelper.ui.components.DurationIconWIthText
import io.ktlab.bshelper.ui.components.MapDiffLabel
import io.ktlab.bshelper.ui.components.MapOnlinePreview
import io.ktlab.bshelper.ui.components.MapperIconWIthText
import io.ktlab.bshelper.ui.components.NPSIconWIthText
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.HomeUIEvent


@Composable
fun MapCard(
    map: IMap,
    modifier: Modifier = Modifier,
    checked: Boolean,
    multiSelectedMode: Boolean = false,
    onUIEvent: (UIEvent) -> Unit,
) {
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
                    .clickable { onUIEvent(HomeUIEvent.PlayPreviewMusicSegment(map)) },
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
                    .width(100.dp)
                    .padding(end = 16.dp),
                horizontalAlignment = Alignment.End
            ) {
                DurationIconWIthText(text = map.getDuration())
                BPMIconWIthText(text = map.getBPM())
                NPSIconWIthText(text = "%.2f".format(map.getMaxNPS()))
            }
            if (multiSelectedMode) {
                Checkbox(
                        checked = checked,
                        onCheckedChange = { onUIEvent(HomeUIEvent.MapMultiSelected(map)) },
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .align(Alignment.CenterVertically)
                )
            }else {
                var previewDialogOpen by remember { mutableStateOf(false) }

//                val context = LocalContext.current
                MapCardMenu(
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .align(Alignment.CenterVertically),
                    onDelete = {
                        onUIEvent(HomeUIEvent.MultiDeleteAction(setOf(map)))
                    },
                    onMove = {

                    },
                    onPreview = {
//                        val sendIntent: Intent = Intent().apply {
//                            action = Intent.ACTION_SEND
//                            putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
//                            type = "text/plain"
//                        }
//                        val shareIntent = Intent.createChooser(sendIntent, null)
//                        context.startActivity(shareIntent)
//                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://allpoland.github.io/ArcViewer/?id=${map.getID()}"))
//                        context.startActivity(webIntent)
                        previewDialogOpen = true
                    },
                )
                if (previewDialogOpen) {
                    MapOnlinePreview(onDismiss = { previewDialogOpen = false }, mapId = map.getID())
                }
            }
        }
}

//@Composable
//@Preview
//fun PreviewMapCard() {
//    LBHelperTheme {
//        MapCard(map = mapExample, navigationToMapDetail = {})
//    }
//}