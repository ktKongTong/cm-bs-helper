package io.ktlab.bshelper.ui.screens.home.playlist

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.ktlab.bshelper.ui.components.AppAlertDialog

@Composable
fun PlaylistCardMenu(
    modifier: Modifier,
    onExport: ()->Unit,
    onDelete: ()->Unit,
    onEdit: ()->Unit,
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
//                    Toast.makeText(context, "删除歌单", Toast.LENGTH_SHORT).show()
                },
                onClose = { deleteAlertDialog = false },
                triggerBy = {
                    DropdownMenuItem(
                    text = { Text(text = "删除") },
                    onClick = {expanded = false; deleteAlertDialog= true },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Playlist"
                        )
                    })
                }
            )
//            DropdownMenuItem(
//                text = { Text(text = "导出 bplist") },
//                onClick = {onExport();expanded = false },
//                leadingIcon = {
//                Icon(
//                    Icons.Default.ImportExport,
//                    contentDescription = "Export Playlist"
//                )
//            })
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