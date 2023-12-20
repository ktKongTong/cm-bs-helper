package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.event.ToolboxUIEvent

@Composable
fun DirectoryChooser(
    targetPath: String,
    onSelectTargetPath: (String) -> Unit = {},
    onUIEvent: (UIEvent) -> Unit,
) {
    var showDirPicker by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = targetPath,
        onValueChange = { },
        enabled = false,
        label = { Text(text = "目标文件夹") },
        trailingIcon = {
            TextButton(onClick = { showDirPicker = true }) {
                Text(text = "选择文件夹")
            }
        },
    )
    if (showDirPicker) {
        BSDirectoryPicker(showDirPicker) { path ->
            showDirPicker = false
            if (path != null) {
                onUIEvent(ToolboxUIEvent.UpdateManageDir(path))
                onSelectTargetPath(path)
            }
        }
    }
}


@Composable
fun DirectoryChooserV2(
    targetPath: String,
    onSelectTargetPath: (String) -> Unit = {},
    onUIEvent: (UIEvent) -> Unit,
) {
    var showDirPicker by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ){
        OutlinedTextField(
            value = targetPath,
            onValueChange = { },
            enabled = false,
            label = { Text(text = "目标文件夹") },
            trailingIcon = {},
        )
        Spacer(Modifier.weight(1f, false))
        TextButton(onClick = { showDirPicker = true }) {
            Text(text = "选择文件夹")
        }
    }
    if (showDirPicker) {
        BSDirectoryPicker(showDirPicker) { path ->
            showDirPicker = false
            if (path != null) {
                onSelectTargetPath(path)
            }
        }
    }
}
