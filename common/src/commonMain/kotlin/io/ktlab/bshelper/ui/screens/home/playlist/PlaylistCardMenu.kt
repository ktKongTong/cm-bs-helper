package io.ktlab.bshelper.ui.screens.home.playlist

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.ktlab.bshelper.ui.components.AppAlertDialog

@Composable
fun PlaylistCardMenu(
    modifier: Modifier,
    onExport: ()->Unit,
    onExportAsBPList: ()->Unit,
    onDelete: ()->Unit,
    onEdit: ()->Unit,
    onSync: ()->Unit,
) {
    var expanded by remember { mutableStateOf(false) }
//    val context = LocalContext.current
    Box(
        modifier = modifier
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
            text = { Text(text = "同步") },
            onClick = {onSync();expanded = false },
            leadingIcon = {
                Icon(
                    Icons.Default.Sync,
                    contentDescription = "Sync Playlist info"
                )
            })
            DropdownMenuItem(
            text = { Text(text = "编辑") },
            onClick = {onEdit();expanded = false },
            leadingIcon = {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Playlist info"
                )
            })

            var deleteAlertDialog by remember { mutableStateOf(false) }
            AppAlertDialog(
                title = "删除歌单",
                text = "确定要删除歌单吗？",
                openDialog = deleteAlertDialog,
                onConfirm = {
                    onDelete()
                },
                onClose = { deleteAlertDialog = false },
                triggerBy = {
                    DropdownMenuItem(
                        text = { Text(text = "删除") },
                        onClick = {expanded = false; deleteAlertDialog = true },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Playlist"
                            )
                        })
                }
            )
            DropdownMenuItem(
                text = { Text(text = "导出 bplist") },
                onClick = {onExportAsBPList();expanded = false },
                leadingIcon = {
                Icon(
                    Icons.Default.ImportExport,
                    contentDescription = "Export Playlist"
                )
            })
            DropdownMenuItem(
                text = { Text(text = "导出为 key") },
                onClick = {
                    onExport()
                    expanded = false
                },
            leadingIcon = {
                Icon(
                    Icons.Default.ImportExport,
                    contentDescription = "Export Playlist"
                )
            })
        }
    }
}