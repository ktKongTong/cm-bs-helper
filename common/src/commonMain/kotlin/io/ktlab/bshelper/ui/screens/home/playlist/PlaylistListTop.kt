package io.ktlab.bshelper.ui.screens.home.playlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.ui.components.AppAlertDialog
import io.ktlab.bshelper.ui.components.DropDownPlaylistSelector
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.HomeUIEvent

@Composable
fun PlaylistListTop(
    onUIEvent: (UIEvent) -> Unit,
    selectablePlaylists : List<IPlaylist> = emptyList(),
) {
//    searchBar
//    toolbar import bplist

    Box(
        Modifier
            .padding(vertical = 4.dp, horizontal = 16.dp)
    ){

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            ImportPlaylistDialog(
                onUIEvent = onUIEvent,
                selectablePlaylists = selectablePlaylists
            )
            AddPlaylistDialog(onUIEvent = onUIEvent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportPlaylistDialog(
    onUIEvent: (UIEvent) -> Unit,
    selectablePlaylists : List<IPlaylist> = emptyList(),
) {
    var uuid by remember { mutableStateOf("") }

    var playlist by remember { mutableStateOf<IPlaylist?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }
    AppAlertDialog(
        title = "导入歌单",
        openDialog = showImportDialog,
        onConfirm = {
            if (uuid.isNotEmpty() && playlist != null){
                onUIEvent(HomeUIEvent.ImportPlaylist(uuid, playlist!!))
            }
        },
        onCancel = { showImportDialog = false },
        triggerBy = {
            TextButton(onClick = { showImportDialog = true }) {
                Text(text = "导入")
            }
        },
        onClose = { showImportDialog = false },
    ){
        var enable by remember { mutableStateOf(true) }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            val focusManager = LocalFocusManager.current
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                value = uuid,
                onValueChange = { uuid = it },
                label = { Text(text = "key") },
                enabled = enable,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                shape = RoundedCornerShape(10.dp),
            )

            //        选择要导入的歌单

            var addPlaylistDialogOpen by remember { mutableStateOf(false) }
            DropDownPlaylistSelector(
                onUIEvent = onUIEvent,
                modifier = Modifier
                    .fillMaxWidth(),
                onSelectedPlaylist = { playlist = it },
                selectablePlaylists = selectablePlaylists,
                selectedIPlaylist = playlist,
                onCreateNewPlaylist = {
                    addPlaylistDialogOpen = true
                }
            )


        }

    }

    var playlistName by remember { mutableStateOf("") }
//    AppAlertDialog(
//        title = "新增歌单",
//        openDialog = addPlaylistDialogOpen,
//        onConfirm = {
//            if (playlistName.isNotEmpty()){
//                onUIEvent(HomeUIEvent.CreateNewPlaylist(playlistName))
//            }
//            addPlaylistDialogOpen = false
//        },
//        onCancel = { addPlaylistDialogOpen = false },
//        onClose = { addPlaylistDialogOpen = false },
//    ){
//        var enable by remember { mutableStateOf(true) }
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(4.dp),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            val focusManager = LocalFocusManager.current
//            OutlinedTextField(
//                value = playlistName,
//                onValueChange = { playlistName = it },
//                label = { Text(text = "歌单名称") },
//                enabled = enable,
//                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
//                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
//                shape = RoundedCornerShape(10.dp),
//            )
//        }
//    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaylistDialog(
    onUIEvent: (UIEvent) -> Unit,
    triggerBy: @Composable () -> Unit = {},
    onTrigger: () -> Unit = {},
) {
    var showAddPlaylistDialog by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }
    AppAlertDialog(
        title = "新增歌单",
        openDialog = showAddPlaylistDialog,
        onConfirm = {
            if (playlistName.isNotEmpty()){
                onUIEvent(HomeUIEvent.CreateNewPlaylist(playlistName))
            }
            showAddPlaylistDialog = false
        },
        onCancel = { showAddPlaylistDialog = false },
        triggerBy = {
            TextButton(onClick = { showAddPlaylistDialog = true }) {
                Text(text = "新增")
            }
        },
        onClose = { showAddPlaylistDialog = false },
    ){
        var enable by remember { mutableStateOf(true) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val focusManager = LocalFocusManager.current
            OutlinedTextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                label = { Text(text = "歌单名称") },
                enabled = enable,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                shape = RoundedCornerShape(10.dp),
            )
        }
    }
}