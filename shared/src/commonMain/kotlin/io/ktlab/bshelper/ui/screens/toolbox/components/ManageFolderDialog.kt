package io.ktlab.bshelper.ui.screens.toolbox.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.ktlab.bshelper.model.SManageFolder
import io.ktlab.bshelper.ui.components.AppDialog
import io.ktlab.bshelper.ui.components.EmptyContent
import io.ktlab.bshelper.ui.event.GlobalUIEvent
import io.ktlab.bshelper.ui.event.UIEvent

@Composable
fun ManageFolderDialog(
    openState: MutableState<Boolean>,
    manageFolders: List<SManageFolder>,
    onUIEvent: (UIEvent) -> Unit
) {
    if (openState.value) {
        AppDialog(
            openState = openState,
            title = "管理曲包文件夹",
            onConfirm = {},
            content = {
                Column(
                    Modifier.verticalScroll(rememberScrollState())
                ) {
                    if(manageFolders.isEmpty()){
                        EmptyContent()
                    }
                    manageFolders.map {
                        ManageFolderItem(it,onUIEvent)
                    }
                }
            }
        )
    }
}

@Composable
fun ManageFolderItem(
    manageFolder: SManageFolder,
    onUIEvent: (UIEvent) -> Unit
) {
    Row(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(manageFolder.name, style = MaterialTheme.typography.titleMedium)
            Text(manageFolder.path, style = MaterialTheme.typography.bodySmall)
        }
        var confirmOpenState by remember { mutableStateOf(false) }
        Spacer(Modifier.weight(1f, false))
        IconButton(
            onClick = { confirmOpenState = true }
        ) {
            Icon(
                Icons.Rounded.Delete,
                contentDescription = "删除",
                tint = MaterialTheme.colorScheme.error,
            )
        }
        if (confirmOpenState) {
            ConfirmDialog(
                openState = remember { mutableStateOf(true) },
                onConfirmRequest = {
                     onUIEvent(GlobalUIEvent.DeleteManageFolder(manageFolder))
                },
                title = "删除曲包文件夹"
            )
        }

    }
}

@Composable
fun ConfirmDialog(
    openState: MutableState<Boolean>,
    onConfirmRequest: () -> Unit,
    title: String
) {
    if (openState.value) {
        AppDialog(
            openState = openState,
            title = title,
            text = "此操作不会删除本地文件夹，仅从数据库内移除相关数据，可在设置中重新添加，所有相关下载任务也将被删除，确定删除吗？",
            onConfirm = {
                onConfirmRequest()
                openState.value = false
            },
            onCancel = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
        )
    }
}