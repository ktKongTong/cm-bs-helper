package io.ktlab.bshelper.ui.screens.home.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.enums.SyncStateEnum
import io.ktlab.bshelper.model.vo.FSPlaylistVO
import io.ktlab.bshelper.ui.components.AsyncImageWithFallback
import io.ktlab.bshelper.ui.components.MapAmountIconWIthText
import io.ktlab.bshelper.viewmodel.HomeUIEvent

@Composable
fun PlaylistCard(
    playlist: IPlaylist,
    onClick: (String) -> Unit,
    onUIEvent: (HomeUIEvent) -> Unit = {},
    selected : Boolean = false
) {
    Card(
        modifier = Modifier.padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.large,
        colors = if (!selected) CardDefaults.outlinedCardColors() else CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
    ){
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = { onClick(playlist.id) })
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
                    Text(
                        text = playlist.title,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    if ((playlist as FSPlaylistVO).sync == SyncStateEnum.SYNCING) { Text(text = "同步中") }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    MapAmountIconWIthText(text = playlist.getMapAmount().toString())
                }
            }
            Column(
            ) {
                PlaylistCardMenu(
                    modifier = Modifier
                        .padding(start = 2.dp),
                    fsPlaylist =(playlist as FSPlaylistVO).toFSPlaylist(),
                    onExport = { onUIEvent(HomeUIEvent.ExportPlaylistAsKey(playlist)) },
                    onDelete = { onUIEvent(HomeUIEvent.DeletePlaylist(playlist)) },
                    onEdit = { onUIEvent(HomeUIEvent.EditPlaylist(playlist, it)) },
                    onSync = { onUIEvent(HomeUIEvent.SyncPlaylist(playlist)) },
                    onExportAsBPList = { onUIEvent(HomeUIEvent.ExportPlaylistAsBPList(playlist)) }
                )
            }

        }
    }
}