package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.ui.components.chiptextfield.core.Chip
import io.ktlab.bshelper.ui.components.chiptextfield.core.rememberChipTextFieldState
import io.ktlab.bshelper.ui.components.chiptextfield.m3.OutlinedChipTextField
import io.ktlab.bshelper.utils.NewFSPlaylist

private fun isValidFilename(filename: String): Boolean {
    return filename.isNotEmpty() && filename.isNotBlank() && filename.length < 255
            && !filename.matches(Regex("[^\\\\/:*?\"<>|]+")) && !filename.startsWith(".")

}

@Composable
fun FSPlaylistForm(
    fsPlaylist: FSPlaylist? = null,
    checkIfExist : (String) -> Boolean = { false },
    onSubmitFSPlaylist: (FSPlaylist?) -> Unit = {},
    triggerBy: @Composable TriggerScope.() -> Unit = {},
) {
    var description by remember { mutableStateOf(fsPlaylist?.description ?: "") }
    var customTags by remember { mutableStateOf(fsPlaylist?.customTags?.split(",")?.toSet() ?: setOf()) }
    var name by remember { mutableStateOf(fsPlaylist?.name ?: "") }
    AppAlertDialog(
        title = fsPlaylist?.let { "编辑歌单信息" } ?: "新建歌单",
        onConfirm = {
            onSubmitFSPlaylist(
                fsPlaylist?.
            copy(name = name, customTags = customTags.joinToString(","), description = description)
                ?: NewFSPlaylist(name = name, customTags = customTags.joinToString(","), description = description)
            )
        },
        triggerBy = triggerBy,
    ){
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val focusManager = LocalFocusManager.current
                var focusTimes by remember { mutableStateOf(0) }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusTimes += 1 },
                    value = name,
                    onValueChange = { name = it },
                    supportingText = {
                        // if not focus before edit, not show error
                        if (focusTimes <= 1) {
                            return@OutlinedTextField
                        }
                        if (!isValidFilename(name)) {
                            Text(text = "歌单名不合法")
                        }else if(name.isEmpty()) {
                            Text(text = "歌单名不能为空")
                        } else if (name.length > 255) {
                            Text(text = "歌单名过长")
                        } else if (name.startsWith(".")) {
                            Text(text = "歌单名不能以.开头")
                        } else if (name.matches(Regex("[^\\\\/:*?\"<>|]+"))) {
                            Text(text = "歌单名不能包含以下字符: \\/:*?\"<>|")
                        } else if(checkIfExist(name)) {
                            Text(text = "歌单名已存在")
                        }
                   },
                    label = { Text(text = "名称") },
                    shape = MaterialTheme.shapes.large,
                    maxLines = 1,
                )

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val state = rememberChipTextFieldState(
                    chips = customTags.map { Chip(it) }.toMutableList(),
                )
                OutlinedChipTextField(
                    modifier = Modifier.onFocusChanged {
                        if(!it.isFocused) {
                            customTags = state.chips.map { it.text }.toSet()
                        }
                    },
                    state = state,
                    onSubmit = { Chip(it) },
                    chipLeadingIcon = { }, // Show check icon if checked
                    chipTrailingIcon = {chip-> Box(Modifier
                        .clip(CircleShape)
                        .clickable {
                        customTags = customTags - chip.text
                        state.chips = state.chips.filter { it.text != chip.text }.toMutableList()
                    }) {
                        Icon(
                            Icons.Rounded.Clear,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(2.dp)
                                .align(Alignment.Center)
                        )
                    } }, // Hide default close button
                    onChipClick = {},
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    shape = MaterialTheme.shapes.large,
                    label = { Text(text = "标签") },
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
//            val focusManager = LocalFocusManager.current
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(text = "描述") },
                    shape = MaterialTheme.shapes.large,
                    maxLines = 3,
                )
            }
        }
    }
}