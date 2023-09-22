package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.ui.components.DropDownPlaylistSelector
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent
import io.ktlab.bshelper.viewmodel.LocalState

@Composable
fun BSMapCardListHeader(
    count: Int,
    localState: LocalState,
    multiSelectedMode: Boolean,
    multiSelectedBSMap: Set<IMap>,
    onUIEvent: (UIEvent) -> Unit,
){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column {
            Text(text = "total: $count")
            Row{
                Text(
                    text = "target playlist",
                    modifier = Modifier
                        .padding(end = 8.dp).
                        align(Alignment.CenterVertically)
                )
                DropDownPlaylistSelector(
                    onUIEvent = onUIEvent,
                    modifier = Modifier,
                    selectablePlaylists = localState.selectableLocalPlaylists,
                    onSelectedPlaylist = {
                        onUIEvent(BeatSaverUIEvent.ChangeTargetPlaylist(it))
                    },
                )
            }
//            val context  = LocalContext.current
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                if (multiSelectedMode) {
                    Text(text = "selected: ${multiSelectedBSMap.size}")
                    TextButton(onClick = {
                        if (localState.targetPlaylist != null) {
                            onUIEvent(BeatSaverUIEvent.MultiDownload(localState.targetPlaylist))
                            onUIEvent(BeatSaverUIEvent.ChangeMultiSelectMode(false))
                        }else {
//                            Toast.makeText(context, "please select a target playlist😉", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text(text = "Download")
                    }
                }
                IconButton(onClick = {
                    onUIEvent(BeatSaverUIEvent.ChangeMultiSelectMode(!multiSelectedMode))
                }, modifier = Modifier) {
                    if (!multiSelectedMode) {
                        Icon(Icons.Default.QueueMusic, contentDescription = stringResource(MR.strings.multi_select))
                    }else {
                        Icon(Icons.Default.Cancel, contentDescription = stringResource(MR.strings.cancel_multi_select))
                    }
                }
            }
        }
    }
}