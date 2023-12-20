package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.model.vo.BSPlaylistVO
import io.ktlab.bshelper.ui.components.AsyncImageWithFallback
import io.ktlab.bshelper.ui.components.labels.*
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.event.BeatSaverUIEvent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BSPlaylistCard(
    playlist: IPlaylist,
    modifier: Modifier = Modifier,
    onUIEvent: (UIEvent) -> Unit,
    downloadInfo: IDownloadTask.PlaylistDownloadTask?,
) {
    Box(
        modifier =
            modifier
                .widthIn(min = 350.dp)
                .clip(shape = RoundedCornerShape(10.dp)),
    ) {
        Row(
            modifier =
                Modifier
                    .clickable {
                        onUIEvent(BeatSaverUIEvent.OnSelectBSPlaylist(playlist))
                    },
        ) {
            AsyncImageWithFallback(
                modifier =
                    Modifier
                        .padding(8.dp)
                        .size(128.dp, 128.dp)
                        .align(Alignment.Top)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .clickable { },
                source = playlist.getAvatar(),
            )
            Column(
                modifier =
                    Modifier
                        .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = playlist.getName(),
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row {
                    MapperLabel(
                        mapperName = playlist.getAuthor(),
                        onClick = {},
                        verified = (playlist as BSPlaylistVO).owner.verifiedMapper?.let { true } == true,
                        avatarUrl = (playlist as BSPlaylistVO).owner.avatar,
                    )
                    DateLabel(date = playlist.playlist.createdAt, modifier = Modifier.padding(start = 8.dp))
                }
                Row {
                    BSDurationLabel(duration = playlist.getTotalDuration().toString())
                    BSNPSRangeLabel(npsRange = "%.2f - %.2f".format(playlist.getMinNPS(), playlist.getMaxNPS()))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BSThumbUpLabel((playlist as BSPlaylistVO).playlist.upVotes)
                    BSThumbDownLabel(playlist.playlist.downVotes)
                }

                if (downloadInfo != null) {
                    Row(
                        modifier =
                            Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {}
                } else {
                    PlaylistDownloadIconButton(
                        onClick = { onUIEvent(BeatSaverUIEvent.DownloadPlaylist(playlist)) },
                        downloadInfo = downloadInfo,
                    )
                }
            }
        }
    }
}
