package io.ktlab.bshelper.ui.components

//import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.ui.event.UIEvent
import kotlinx.coroutines.delay

//TODO fix keyboard action
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DropDownPlaylistSelector(
    onUIEvent: (UIEvent) -> Unit,
    modifier: Modifier = Modifier,
    onCreateNewPlaylist: () -> Unit = {},
    onSelectedPlaylist: (IPlaylist?) -> Unit = {},
    selectablePlaylists: List<IPlaylist> = emptyList(),
){

    var expanded by remember { mutableStateOf(false) }
    var selectedIPlaylist by remember { mutableStateOf<IPlaylist?>(null) }
    var searchKey by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
            .fillMaxWidth(),
    ) {

        val focusManager = LocalFocusManager.current
        val onClose = {
            expanded = false
            if (selectedIPlaylist == null) {
                searchKey = ""
            }
            focusManager.clearFocus()
        }
        val onOpen = {
            expanded = true
            searchKey = ""
        }
        val focusState = remember { mutableStateOf(false) }
        OutlinedTextField(
            modifier = modifier
                .menuAnchor()
                .onFocusChanged {
                    focusState.value = it.isFocused
                    if (it.isFocused) {
                        onOpen()
                    }
                }
            ,
            value = if (focusState.value) searchKey else selectedIPlaylist?.getName() ?: ""
            ,
            onValueChange = { searchKey = it },
            placeholder = {
                Text(
                    modifier = Modifier.alpha(0.5f),
                    text = selectedIPlaylist?.getName() ?: "",
                )
            },
            label = { Text("目标歌单") },
            suffix = {
                if (focusState.value) {
                    Icon(
                        Icons.Default.Clear,
                        modifier = Modifier
                            .clickable {
                                searchKey = ""
                            },
                        contentDescription = "")
                }else if(selectedIPlaylist != null){
                    Icon(
                        Icons.Default.Clear,
                        modifier = Modifier
                            .clickable {
                               selectedIPlaylist = null
                                onClose()
                            },
                        contentDescription = "")
                }

            },
            prefix = {Icon(Icons.Default.Search, contentDescription = "")},
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus();
                onClose()

            }),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onClose,
            modifier = Modifier
                .heightIn(0.dp, 250.dp)
                .alpha(0.95f)
                .verticalScroll(rememberScrollState())

        ) {

            val searchedSelectablePlaylist = selectablePlaylists.filter {
                searchKey.ifEmpty { return@filter true }
                it.getPlaylistDescription().contains(searchKey, ignoreCase = true) ||
                        it.getName().contains(searchKey, ignoreCase = true)
            }
//            DropdownMenuItem(
//                text = {
//                    Column {
//
//                    }
//                }, onClick = {})

            Column {
                if (searchedSelectablePlaylist.isEmpty()){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImageWithFallback(source = "", modifier = Modifier.weight(1f,false))
                        Text(text = "没有找到歌单", style = MaterialTheme.typography.bodyMedium)
                    }
                }


                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    onClick = { onCreateNewPlaylist() }) {
                    Text(text = "新建歌单")
                }


                if (searchedSelectablePlaylist.isNotEmpty()) {
                    searchedSelectablePlaylist.forEach{
                        val playlist = it
                        DropdownMenuItem(
                            text = { Text(playlist.getName()) },
                            onClick = {
                                selectedIPlaylist = playlist
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
}


//@Preview
//@Composable
//fun DropDownPlaylistSelectorPreview() {
////    DropDownPlaylistSelector(
////        onUIEvent = {},
////        selectablePlaylists = listOf(playlistViewExample, playlistViewExample,
////            playlistViewExample, playlistViewExample, playlistViewExample,
////            playlistViewExample, playlistViewExample, playlistViewExample,
////            playlistViewExample)
////    )
//}