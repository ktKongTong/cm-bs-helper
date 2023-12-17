package io.ktlab.bshelper.ui.screens.home.playlist

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.ui.components.AppDialog
import io.ktlab.bshelper.ui.components.FSPlaylistFormV2
import io.ktlab.bshelper.ui.screens.beatsaver.components.IconExposedDropDownMenu

@Composable
fun PlaylistCardMenu(
    modifier: Modifier,
    fsPlaylist: FSPlaylist,
    onExport: () -> Unit,
    onExportAsBPList: () -> Unit,
    onDelete: () -> Unit,
    onEdit: (FSPlaylist) -> Unit,
    onSync: () -> Unit,
) {
    val playlistFormOpenState = remember { mutableStateOf(false) }
    val deleteDialogOpenState = remember { mutableStateOf(false) }
    // will cause embedded composable error
    // outer closed then inner also closed
    IconExposedDropDownMenu(modifier) {
        DropdownMenuItem(
            text = { Text(text = "同步") },
            onClick = {
                onTrigger()
                onSync()
            },
            leadingIcon = { Icon(Icons.Default.Sync, contentDescription = "Sync Playlist info") },
        )
        DropdownMenuItem(
            text = { Text(text = "编辑") },
            onClick = {
                onTrigger()
                playlistFormOpenState.value = true
            },
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Edit Playlist info") },
        )
        DropdownMenuItem(
            text = { Text(text = "删除") },
            onClick = {
                onTrigger()
                deleteDialogOpenState.value = true
            },
            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = "Delete Playlist") },
        )

        DropdownMenuItem(
            text = { Text(text = "导出为 bplist") },
            onClick = {
                onTrigger()
                onExportAsBPList()
            },
            leadingIcon = { Icon(Icons.Default.ImportExport, contentDescription = "Export Playlist AS bplist") },
        )
        DropdownMenuItem(
            text = { Text(text = "导出为 key") },
            onClick = {
                onTrigger()
                onExport()
            },
            leadingIcon = { Icon(Icons.Default.ImportExport, contentDescription = "Export Playlist as key") },
        )
    }

    //

    AppDialog(
        title = "删除歌单",
        text = "确定要删除歌单吗？",
        onConfirm = { onDelete() },
        openState = deleteDialogOpenState,
    )

    FSPlaylistFormV2(
        fsPlaylist = fsPlaylist,
        openState = playlistFormOpenState,
    )
}
