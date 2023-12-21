package io.ktlab.bshelper.ui.screens.toolbox.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.ManageFolderBackup
import io.ktlab.bshelper.model.SManageFolder
import io.ktlab.bshelper.ui.LocalUIEventHandler
import io.ktlab.bshelper.ui.components.AppDialog
import io.ktlab.bshelper.ui.event.ToolboxUIEvent

@Composable
fun BackUp(
    manageFolders:List<SManageFolder>,
    backups:List<ManageFolderBackup>
) {
    val backupDialogOpen = remember { mutableStateOf(false) }
    val restoreDialogOpen = remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().padding(16.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("备份与恢复", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.weight(1f,false))
        Row {
            TextButton(
                onClick = { backupDialogOpen.value = true }
            ) {
                Text("备份")
            }

            TextButton(onClick = { restoreDialogOpen.value = true }) {
                Text("恢复")
            }
        }
    }
    BackUpDialog(backupDialogOpen,manageFolders)
    RestoreDialog(restoreDialogOpen,backups)
}

@Composable
fun BackUpDialog(
    openState: MutableState<Boolean>,
    manageFolders:List<SManageFolder>,
) {
    val onUIEvent = LocalUIEventHandler.current
    AppDialog(
        openState = openState,
        title = "备份",
        onConfirm = {}
    ) {
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            val enabled = remember { mutableStateOf(true) }
            manageFolders.forEach {
                ManageFolderItem(it,enabled.value){
                    onUIEvent(ToolboxUIEvent.BackUpManageFolder(it))
                    enabled.value = false
                }
            }
        }
    }

}

@Composable
fun RestoreDialog(
    openState: MutableState<Boolean>,
    backups:List<ManageFolderBackup>
) {
    val onUIEvent = LocalUIEventHandler.current
    AppDialog(
        openState = openState,
        title = "恢复",
        onConfirm = {}
    ) {
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            val enabled = remember { mutableStateOf(true) }
            backups.forEach {
                BackUpItem(it,enabled.value){
                    onUIEvent(ToolboxUIEvent.RestoreManageFolder(it))
                    enabled.value = false
                }
            }
        }
    }
}

@Composable
private fun BackUpItem(
    backup: ManageFolderBackup,
    enabled: Boolean = true,
    onRestore: (ManageFolderBackup) -> Unit,
){
    Row(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.widthIn(max=200.dp)
        ) {
            Text("${backup.manageFolder.name} - ${backup.manageFolder.gameType}", style = MaterialTheme.typography.titleMedium, overflow = TextOverflow.Ellipsis, maxLines = 1)
            Row {
                Text("备份于: ", style = MaterialTheme.typography.bodySmall)
                Text(backup.manageFolder.path,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            }
        }
        Spacer(Modifier.weight(1f, false))
        IconButton(
            onClick = { onRestore((backup)) },
            enabled = enabled
        ) {
            Icon(
                Icons.Rounded.Restore,
                contentDescription = "恢复",
            )
        }
    }
}

@Composable
private fun ManageFolderItem(
    manageFolder: SManageFolder,
    enabled: Boolean = true,
    onBackup: (SManageFolder) -> Unit,
) {

    Row(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.widthIn(max=200.dp)
        ) {
            Text("${manageFolder.name} - ${manageFolder.gameType}", style = MaterialTheme.typography.titleMedium, overflow = TextOverflow.Ellipsis, maxLines = 1)
            Text(manageFolder.path, style = MaterialTheme.typography.bodySmall, overflow = TextOverflow.Ellipsis, maxLines = 1)
        }
        Spacer(Modifier.weight(1f, false))
        IconButton(
            onClick = { onBackup((manageFolder)) },
            enabled = enabled
        ) {
            Icon(
                Icons.Rounded.Backup,
                contentDescription = "备份",
            )
        }
    }
}