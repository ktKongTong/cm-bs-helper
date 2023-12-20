package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.event.BeatSaverUIEvent
import io.ktlab.bshelper.ui.event.GlobalUIEvent
import io.ktlab.bshelper.ui.viewmodel.LocalState

@Composable
fun BSMapCardListHeader(
    count: Int,
    localState: LocalState,
    multiSelectedMode: Boolean,
    multiSelectedBSMap: Set<IMap>,
    onUIEvent: (UIEvent) -> Unit,
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Column {
            Text(text = "Maps", style = MaterialTheme.typography.titleLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (multiSelectedMode) Arrangement.SpaceBetween else Arrangement.End,
            ) {
                if (multiSelectedMode) {
                    Text(text = "已选中: ${multiSelectedBSMap.size}", modifier = Modifier.align(Alignment.CenterVertically))
                    TextButton(onClick = {
                        if (localState.targetPlaylist != null) {
                            onUIEvent(BeatSaverUIEvent.MultiDownload(localState.targetPlaylist))
                        } else {
                            onUIEvent(GlobalUIEvent.ShowSnackBar("请选择目标歌单"))
                        }
                    }) {
                        Text(text = "Download")
                    }
                }
                Box {
                    IconButton(onClick = {
                        onUIEvent(BeatSaverUIEvent.ChangeMultiSelectMode(!multiSelectedMode))
                    }, modifier = Modifier) {
                        if (!multiSelectedMode) {
                            Icon(Icons.Default.QueueMusic, contentDescription = stringResource(MR.strings.multi_select))
                        } else {
                            Icon(Icons.Default.Cancel, contentDescription = stringResource(MR.strings.cancel_multi_select))
                        }
                    }
                }
            }
        }
    }
}
