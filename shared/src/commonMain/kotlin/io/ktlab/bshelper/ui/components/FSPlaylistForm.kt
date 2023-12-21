package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.ui.LocalUserPreference
import io.ktlab.bshelper.ui.components.chiptextfield.core.Chip
import io.ktlab.bshelper.ui.components.chiptextfield.core.rememberChipTextFieldState
import io.ktlab.bshelper.ui.components.chiptextfield.m3.OutlinedChipTextField
import io.ktlab.bshelper.utils.isValidFilename
import io.ktlab.bshelper.utils.newFSPlaylist


@Composable
fun FSPlaylistFormV2(
    fsPlaylist: FSPlaylist? = null,
    checkIfExist: (String) -> Boolean = { false },
    onSubmitFSPlaylist: (FSPlaylist?) -> Unit = {},
    openState: MutableState<Boolean>,
) {
    var description by remember { mutableStateOf(fsPlaylist?.description ?: "") }
    var customTags by remember { mutableStateOf(fsPlaylist?.customTags?.split(",")?.toSet() ?: setOf()) }
    var name by remember { mutableStateOf(fsPlaylist?.name ?: "") }
    val userPreference = LocalUserPreference.current
    AppDialog(
        title = fsPlaylist?.let { "编辑歌单信息" } ?: "新建歌单",
        onConfirm = {
            onSubmitFSPlaylist(
                fsPlaylist
                    ?.copy(name = name, customTags = customTags.joinToString(","), description = description)
                    ?: newFSPlaylist(name = name, customTags = customTags.joinToString(","), description = description, manageDirId = userPreference.currentManageFolder?.id!!),
            )
        },
        openState = openState,
    ) {
        Column {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    value = name,
                    onValueChange = { name = it },
                    supportingText = {
                        // only show when edit
                        if (!name.isValidFilename()) {
                            Text(text = "歌单名不合法")
                        } else if (name.isEmpty()) {
                            Text(text = "歌单名不能为空")
                        } else if (name.length > 255) {
                            Text(text = "歌单名过长")
                        } else if (name.startsWith(".")) {
                            Text(text = "歌单名不能以.开头")
                        } else if (name.matches(Regex("[^\\\\/:*?\"<>|]+"))) {
                            Text(text = "歌单名不能包含以下字符: \\/:*?\"<>|")
                        } else if (checkIfExist(name)) {
                            Text(text = "歌单名已存在")
                        }
                    },
                    label = { Text(text = "名称") },
                    shape = MaterialTheme.shapes.large,
                    maxLines = 1,
                )
            }
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val state =
                    rememberChipTextFieldState(
                        chips = customTags.map { Chip(it) }.toMutableList(),
                    )
                OutlinedChipTextField(
                    modifier =
                        Modifier.onFocusChanged {
                            if (!it.isFocused) {
                                customTags = state.chips.map { it.text }.toSet()
                            }
                        },
                    state = state,
                    onSubmit = { Chip(it) },
                    chipLeadingIcon = { },
                    chipTrailingIcon = { chip ->
                        Box(
                            Modifier
                                .clip(CircleShape)
                                .clickable {
                                    customTags = customTags - chip.text
                                    state.chips = state.chips.filter { it.text != chip.text }.toMutableList()
                                },
                        ) {
                            Icon(
                                Icons.Rounded.Clear,
                                contentDescription = "",
                                modifier =
                                    Modifier
                                        .padding(2.dp)
                                        .align(Alignment.Center),
                            )
                        }
                    },
                    onChipClick = {},
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    shape = MaterialTheme.shapes.large,
                    label = { Text(text = "标签") },
                )
            }
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
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
