package io.ktlab.bshelper.ui.screens.toolbox

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import io.ktlab.bshelper.model.vo.ScanState
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.ToolboxUIEvent
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import io.ktlab.bshelper.ui.screens.toolbox.components.ScanPlaylistDialog

@Composable
expect fun isStoragePermissionGranted():Boolean


@Composable
expect fun RequestStoragePermission()

@Composable
fun ScanScreen(
    scanState: ScanState,
    onUIEvent: (UIEvent) -> Unit,
){
    Column (
        modifier = Modifier.fillMaxSize()
    ){

        var requestPermission by remember { mutableStateOf(false) }

        if (requestPermission) {
            RequestStoragePermission()
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            var dialogOpen by remember { mutableStateOf(false) }
            val isStoragePermissionGranted = isStoragePermissionGranted()
            TextButton(onClick = {
                // TODO: check if storage permission is granted
                if(!isStoragePermissionGranted){
                    requestPermission = true
                    return@TextButton
                }
                dialogOpen = true
            }) {
                Text(text = "scan playlist")
            }
            if(dialogOpen) {
                ScanPlaylistDialog(
                    scanState = scanState,
                    onUIEvent = onUIEvent,
                    onCloseDialog = { dialogOpen = false;onUIEvent(ToolboxUIEvent.ClearScanState) }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = {
                onUIEvent(ToolboxUIEvent.ClearLocalData)
            }) {
                Text(text = "clear local data")
            }
        }
    }
}