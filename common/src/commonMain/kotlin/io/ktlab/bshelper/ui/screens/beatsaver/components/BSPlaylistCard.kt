package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import io.ktlab.bshelper.model.dto.BSPlaylistDTO
import io.ktlab.bshelper.model.enums.MapTag
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.model.vo.BSPlaylistVO
import io.ktlab.bshelper.ui.components.*
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BSPlaylistCard(
    playlist: IPlaylist,
    modifier: Modifier = Modifier,
    onUIEvent: (UIEvent) -> Unit,
) {
    Box(
        modifier = modifier
            .widthIn(min = 350.dp)
            .clip(shape = RoundedCornerShape(10.dp))
    ){
        Row(
            modifier = Modifier
                .combinedClickable(
                    onLongClick = {},
                    onClick = {}
                )
        ) {
            AsyncImageWithFallback(
                modifier = Modifier
                    .padding(8.dp)
                    .size(128.dp, 128.dp)
                    .align(Alignment.Top)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .clickable { },
                source = playlist.getAvatar(),
            )
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = playlist.getName(),
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row {
                    if ((playlist as BSPlaylistVO).owner.verifiedMapper?.let { true } == true) {
                        Icon(
                            Icons.Filled.Verified,
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Verified Mapper",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    MapperIconWIthText(text = playlist.getAuthor())
                    Spacer(modifier = Modifier.width(8.dp))
                    DurationIconWIthText(text = playlist.getTotalDuration().toString())
                    Spacer(modifier = Modifier.width(8.dp))
                    NPSIconWIthText(text = "%.2f - %.2f".format(playlist.getMinNPS(), playlist.getMaxNPS()))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        ThumbUpIconWIthText(text = (playlist as BSPlaylistVO).playlist.upVotes.toString())
                        Spacer(modifier = Modifier.width(8.dp))
                        ThumbDownIconWIthText(text = playlist.playlist.downVotes.toString())
                    }
                }

                DownloadIconButton(
                    onClick = {
    //                        onUIEvent(BeatSaverUIEvent.DownloadPlaylist(playlist))
                    },
                    downloadInfo = null,
                )
            }
        }
    }
}