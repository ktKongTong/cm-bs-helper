package io.ktlab.bshelper.ui.screens.toolbox.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.vo.*
import io.ktlab.bshelper.ui.components.CancelButton
import io.ktlab.bshelper.ui.components.ConfirmButton
import io.ktlab.bshelper.ui.components.NextStepIconButton
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.ToolboxUIEvent

private fun getStepByCurrentState(state:ScanStateEventEnum):Int {
    return when(state) {
        ScanStateEventEnum.NOT_START -> 0
        ScanStateEventEnum.SCANNING -> 1
        ScanStateEventEnum.SCAN_COMPLETE -> 2
        ScanStateEventEnum.SCAN_ERROR -> 3
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanPlaylistDialog(
    scanState: ScanStateV2,
    onUIEvent: (UIEvent) -> Unit,
    onCloseDialog : () -> Unit = {}
){
    AlertDialog(
        onDismissRequest = {},
        Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(10)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = scanState.state.human,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
            }
            Column (
                modifier = Modifier
                    .padding( horizontal = 32.dp)
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
//                StepsProgressBar(numberOfSteps = 4, currentStep = getStepByCurrentState(scanState.state))
                var targetPath by remember { mutableStateOf("") }
                Box(modifier = Modifier
                ) {
                    StepContent(scanState,targetPath,{targetPath = it},onUIEvent)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    when (scanState.state) {
                        ScanStateEventEnum.NOT_START -> {
                            CancelButton { onCloseDialog() }
                            NextStepIconButton { onUIEvent(ToolboxUIEvent.ScanPlaylist(targetPath)) }
                        }
                        ScanStateEventEnum.SCANNING -> {
//                            CancelButton { onCloseDialog() }
//                            ConfirmButton { onUIEvent(ToolboxUIEvent.ScanSelectedPlaylist) }
                        }
                        ScanStateEventEnum.SCAN_COMPLETE,ScanStateEventEnum.SCAN_ERROR -> {
                            ConfirmButton { onCloseDialog() }
                        }
                    }
                }
            }

        }
    }
}


@Composable
fun StepContent(
    scanState: ScanStateV2,
    targetPath: String,
    onSelectTargetPath: (String) -> Unit,
    onUIEvent: (UIEvent) -> Unit
){
    when(scanState.state) {
            ScanStateEventEnum.NOT_START -> {
//                DirectoryChooser(targetPath,onSelectTargetPath,onUIEvent)
            }
            ScanStateEventEnum.SCANNING,ScanStateEventEnum.SCAN_COMPLETE,ScanStateEventEnum.SCAN_ERROR -> {
                Column {
                    Text(text = "文件夹：${targetPath}")
                    if (scanState.state == ScanStateEventEnum.SCAN_COMPLETE || scanState.state == ScanStateEventEnum.SCAN_ERROR){
                        Text(text = "扫描完成")
                    }
//                    else {
                        Text(text = "扫描中，请稍后 ${scanState.scannedDirCount}/${scanState.totalDirCount}")
                        Text(text = "当前歌单：${scanState.currentPlaylistDir},已扫描${scanState.scannedMapCount}", maxLines = 1)
                        Text(text = "当前谱面：${scanState.currentMapDir}", maxLines = 1)
//                    }

                    LazyColumn {
                        items(scanState.playlistScanList.size) { index ->
                            val item = scanState.playlistScanList.reversed()[index]

                            item.value.let { playlistScanState ->
                                var animatedVisibility by remember { mutableStateOf(false) }
                                Row {
                                    Text(text = playlistScanState.playlistName)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(text = "${playlistScanState.scannedFileAmount}/${playlistScanState.fileAmount}")

                                    if (playlistScanState.errorStates.isNotEmpty()) {
                                        IconButton(
                                            onClick = { animatedVisibility = !animatedVisibility }
                                        ) {
                                            Icon(Icons.Filled.Error, contentDescription = null)
                                        }
                                    }
                                }
                                    AnimatedVisibility(animatedVisibility) {
                                        if (playlistScanState.errorStates.isNotEmpty()){
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f, fill = false),
                                                horizontalArrangement = Arrangement.End,
                                            ){
                                                Column {
                                                        playlistScanState.errorStates.map {
                                                            Row (
                                                                Modifier.horizontalScroll(rememberScrollState())
                                                            ){
                                                                Text(
                                                                    text = it.toString(),
                                                                    maxLines = 1,
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
            }
        }
}

@Composable
fun DirectoryChooser(
    targetPath: String,
    onSelectTargetPath: (String) -> Unit = {},
    onUIEvent: (UIEvent) -> Unit
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
        }
    )
    if (showDirPicker) {
        BSDirectoryPicker(showDirPicker){ path ->
            showDirPicker = false
            if (path != null) {
                onUIEvent(ToolboxUIEvent.UpdateDefaultManageDir(path))
                onSelectTargetPath(path)
            }
        }
    }

}

@Composable
fun ScanPlaylistCard(
    globalScanStateEnum: GlobalScanStateEnum,
    playlistScanState: PlaylistScanState,
    onUIEvent: (UIEvent) -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (globalScanStateEnum != GlobalScanStateEnum.SCAN_PLAYLISTS_COMPLETE) {
                    return@clickable
                }
//                onUIEvent(ToolboxUIEvent.SelectPlaylistTobeScan(playlistScanState))
            },
        colors = CardDefaults.cardColors(containerColor = if (playlistScanState.state!= PlaylistScanStateEnum.UNSELECTED) { Color.LightGray } else { Color.Transparent })
    ) {
        when(playlistScanState.state){
            PlaylistScanStateEnum.UNSELECTED,PlaylistScanStateEnum.SELECTED_BUT_NOT_START -> {
                Row {
                    Text(text = playlistScanState.playlistName)
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            PlaylistScanStateEnum.SCAN_ERROR -> {
                Row {
                    Text(text = playlistScanState.playlistName)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "0/${playlistScanState.possibleMapAmount}")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        horizontalArrangement = Arrangement.End,
                    ){
                        Icon(Icons.Filled.Error, contentDescription = null)
                    }
                }
            }
            PlaylistScanStateEnum.SCANNING,PlaylistScanStateEnum.SCAN_COMPLETE -> {
                val finishedScanMap = playlistScanState.mapScanStates.filter {
                    it.state == ScanStateEnum.SCAN_COMPLETE || it.state == ScanStateEnum.SCAN_ERROR
                }
                val progress by remember { mutableFloatStateOf(finishedScanMap.size.toFloat()/playlistScanState.possibleMapAmount) }
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                    label = ""
                )
                Row {
                    Text(text = playlistScanState.playlistName)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "${finishedScanMap.size}/${playlistScanState.possibleMapAmount}")
                    if (playlistScanState.state == PlaylistScanStateEnum.SCANNING){
                        CircularProgressIndicator(progress = animatedProgress)
                    }else{
                        Icon(Icons.Filled.Check, contentDescription = null)
                    }
                }

            }
        }

    }
}