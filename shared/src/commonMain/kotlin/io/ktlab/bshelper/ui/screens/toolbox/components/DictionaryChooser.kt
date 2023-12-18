package io.ktlab.bshelper.ui.screens.toolbox.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.viewmodel.ToolboxUIEvent

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
                onUIEvent(ToolboxUIEvent.UpdateDefaultManageDir(path))
                onSelectTargetPath(path)
            }
        }
    }
}
