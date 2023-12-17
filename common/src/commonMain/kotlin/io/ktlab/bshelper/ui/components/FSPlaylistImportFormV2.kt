package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.HomeUIEvent

@Composable
fun FSPlaylistImportFormV2(
    onUIEvent: (UIEvent) -> Unit,
    selectablePlaylists: List<IPlaylist> = emptyList(),
    openState: MutableState<Boolean>,
) {
    var uuid by remember { mutableStateOf("") }

    var playlist by remember { mutableStateOf<IPlaylist?>(null) }
    AppDialog(
        title = "导入歌单",
        onConfirm = {
            uuid.isNotEmpty().takeIf { playlist != null }?.let {
                onUIEvent(HomeUIEvent.ImportPlaylist(uuid, playlist!!))
            }
        },
        openState = openState,
    ) {
        var enable by remember { mutableStateOf(true) }
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val focusManager = LocalFocusManager.current
            OutlinedTextField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                value = uuid,
                onValueChange = { uuid = it },
                label = { Text(text = "key") },
                enabled = enable,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                shape = MaterialTheme.shapes.large,
            )
            //        选择要导入的歌单
            DropDownPlaylistSelector(
                onUIEvent = onUIEvent,
                modifier = Modifier.fillMaxWidth(),
                onSelectedPlaylist = { playlist = it },
                selectablePlaylists = selectablePlaylists,
                selectedIPlaylist = playlist,
            )
        }
    }
}
