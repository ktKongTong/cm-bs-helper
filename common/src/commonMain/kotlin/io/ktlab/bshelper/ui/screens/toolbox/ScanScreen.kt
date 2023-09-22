package io.ktlab.bshelper.ui.screens.toolbox

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import io.ktlab.bshelper.model.vo.ScanState
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.ToolboxUIEvent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.ktlab.bshelper.ui.screens.toolbox.components.ScanPlaylistDialog

@Composable
expect fun IsStoragePermissionGranted():Boolean

@Composable
private fun RequestStoragePermission() {
    val storagePermission = IsStoragePermissionGranted()
    LaunchedEffect(key1 = true) {
        if (!storagePermission) {
//            launcher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }
}

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
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            var dialogOpen by remember { mutableStateOf(false) }
            val isStoragePermissionGranted = IsStoragePermissionGranted()
            TextButton(onClick = {
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
    }
}