package io.ktlab.bshelper.ui.screens.home.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource

import io.ktlab.bshelper.MR
import io.ktlab.bshelper.ui.components.AsyncImageWithFallback
import io.ktlab.bshelper.ui.components.MapAmountIconWIthText
import io.ktlab.bshelper.ui.components.NPSIconWIthText
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.ui.components.AsyncImageWithFallback
import io.ktlab.bshelper.ui.components.MapAmountIconWIthText
import io.ktlab.bshelper.ui.components.NPSIconWIthText
import io.ktlab.bshelper.viewmodel.HomeUIEvent

@Composable
fun PlaylistCard(
    playlist: IPlaylist,
    onClick: (String) -> Unit,
    onUIEvent: (HomeUIEvent) -> Unit = {},
    selected : Boolean = false
) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 16.dp)
            .clickable(onClick = { onClick(playlist.id) }),
        shape = RoundedCornerShape(10.dp),
    colors = if (!selected) CardDefaults.outlinedCardColors() else CardDefaults.cardColors()
    ){
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            AsyncImageWithFallback(
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .padding(end = 16.dp),
                source = playlist.getImage()
            )
            Column (
                Modifier.weight(1f, fill = false)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = playlist.title)
                    if (!playlist.isCustom()) {
                            Text(
                                text = stringResource(MR.strings.playlist_bs),
                                modifier = Modifier
                            )
                    }else {
                        Text(text = "custom")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val minNps = String.format("%.2f", playlist.getMinNPS())
                    val maxNps = String.format("%.2f", playlist.getMaxNPS())
                    NPSIconWIthText(text = "$minNps - $maxNps")
                    Spacer(modifier = Modifier.width(8.dp))
//                    DurationIconWIthText(text = totalDuration)
                    MapAmountIconWIthText(text = playlist.getMapAmount().toString())
                }
            }
            Column(
            ) {
                PlaylistCardMenu(
                    modifier = Modifier
                        .padding(start = 2.dp),
                    onExport = {
                        onUIEvent(HomeUIEvent.ExportPlaylistAsKey(playlist))
                    },
                    onDelete = {
                        onUIEvent(HomeUIEvent.DeletePlaylist(playlist))
                    },
                    onEdit = {
                        onUIEvent(HomeUIEvent.EditPlaylist(playlist))
                    }
                )
            }

        }
    }
}
//@Preview("PlaylistCard")
//@Composable
//fun BookmarkButtonPreview() {
//    BSHelperTheme {
//        Surface {
//            PlaylistCard(playlistViewExample, {})
//        }
//    }
//}