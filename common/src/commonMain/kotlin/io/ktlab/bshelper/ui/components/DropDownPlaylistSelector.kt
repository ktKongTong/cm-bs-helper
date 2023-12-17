package io.ktlab.bshelper.ui.components

// import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.GlobalUIEvent

// TODO fix keyboard action
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DropDownPlaylistSelector(
    onUIEvent: (UIEvent) -> Unit,
    modifier: Modifier = Modifier,
    onSelectedPlaylist: (IPlaylist?) -> Unit = {},
    selectablePlaylists: List<IPlaylist> = emptyList(),
    selectedIPlaylist: IPlaylist? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    var searchKey by remember { mutableStateOf("") }

    val searchedSelectablePlaylist =
        selectablePlaylists.filter {
            searchKey.ifEmpty { return@filter true }
            it.getPlaylistDescription().contains(searchKey, ignoreCase = true) ||
                it.getName().contains(searchKey, ignoreCase = true)
        }
    val playlistFormOpenState = remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth(),
    ) {
        val focusManager = LocalFocusManager.current
        val onClose = {
            expanded = false
            if (selectedIPlaylist == null) {
                searchKey = ""
            }
//            focusManager.clearFocus()
        }
        val onOpen = {
            expanded = true
            searchKey = ""
        }
        val focusState = remember { mutableStateOf(false) }
        OutlinedTextField(
            modifier =
                modifier
                    .menuAnchor()
                    .onFocusChanged {
                        focusState.value = it.isFocused
                        if (it.isFocused) {
                            onOpen()
                        }
                    },
            value = if (focusState.value) searchKey else selectedIPlaylist?.getName() ?: "",
            onValueChange = { searchKey = it },
            placeholder = {
                Text(
                    modifier = Modifier.alpha(0.5f),
                    text = selectedIPlaylist?.getName() ?: "",
                )
            },
            label = { Text("目标歌单") },
            suffix = {
                if (focusState.value || selectedIPlaylist != null) {
                    Box(
                        Modifier.clip(CircleShape).clickable {
                            if (focusState.value) {
                                searchKey = ""
                            } else {
                                onSelectedPlaylist(null)
                            }
                            onClose()
                        },
                    ) {
                        Icon(
                            Icons.Rounded.Clear,
                            modifier =
                                Modifier
                                    .padding(2.dp),
                            contentDescription = "",
                        )
                    }
                }
            },
            keyboardActions =
                KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    onClose()
                }),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            singleLine = true,
            shape = MaterialTheme.shapes.large,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onClose,
            modifier =
                Modifier
                    .heightIn(0.dp, 250.dp)
                    .alpha(0.95f)
                    .verticalScroll(rememberScrollState()),
        ) {
            Column {
                if (searchedSelectablePlaylist.isEmpty()) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        DefaultImage(modifier = Modifier.weight(1f, false))
                        Text(text = "没有找到歌单", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                TextButton(
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                    onClick = {
                        onClose()
                        playlistFormOpenState.value = true
                    },
                ) {
                    Text(text = "新建歌单")
                }
                if (searchedSelectablePlaylist.isNotEmpty()) {
                    searchedSelectablePlaylist.forEach {
                        val playlist = it
                        DropdownMenuItem(
                            text = { Text(playlist.getName()) },
                            onClick = {
                                onSelectedPlaylist(playlist)
                                onClose()
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
    }
    FSPlaylistFormV2(
        onSubmitFSPlaylist = { it?.let { onUIEvent(GlobalUIEvent.CreatePlaylist(it)) } },
        checkIfExist = { name -> selectablePlaylists.any { it.getName() == name } },
        openState = playlistFormOpenState,
    )
}
