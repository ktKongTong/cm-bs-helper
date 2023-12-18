package io.ktlab.bshelper.ui.screens.toolbox

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.vo.ScanStateEventEnum
import io.ktlab.bshelper.model.vo.ScanStateV2
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.toolbox.components.DirectoryChooser
import io.ktlab.bshelper.ui.viewmodel.ToolboxUIEvent

@Composable
expect fun isStoragePermissionGranted(): Boolean

@Composable
expect fun RequestStoragePermission()

@Composable
fun ScanScreen(
    scanState: ScanStateV2,
    onUIEvent: (UIEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        var requestPermission by remember { mutableStateOf(false) }

        var targetPath by remember { mutableStateOf("") }
        if (requestPermission) {
            RequestStoragePermission()
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("扫描曲包", style = MaterialTheme.typography.headlineLarge)
            IconButton(onClick = {
                onUIEvent(ToolboxUIEvent.ClearLocalData)
                targetPath = ""
            }) {
                Icon(Icons.Rounded.Delete, contentDescription = "delete map")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            val isStoragePermissionGranted = isStoragePermissionGranted()
            DirectoryChooser(targetPath, { targetPath = it }, onUIEvent)
            TextButton(onClick = {
                // TODO: check if storage permission is granted
                if (!isStoragePermissionGranted) {
                    requestPermission = true
                    return@TextButton
                }
                onUIEvent(ToolboxUIEvent.ScanPlaylist(targetPath))
            }) {
                Text(text = "扫描")
            }
        }
        if (targetPath.isNotEmpty()) {
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxHeight(0.7f)
                            .weight(1f, fill = false),
                ) {
                    StepContent(scanState, targetPath, { targetPath = it }, onUIEvent)
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
    onUIEvent: (UIEvent) -> Unit,
) {
    if (scanState.state == ScanStateEventEnum.SCANNING ||
        scanState.state == ScanStateEventEnum.SCAN_COMPLETE ||
        scanState.state == ScanStateEventEnum.SCAN_ERROR
    ) {
        Column {
            Text(text = "文件夹：$targetPath")
            if (scanState.state == ScanStateEventEnum.SCAN_COMPLETE || scanState.state == ScanStateEventEnum.SCAN_ERROR) {
                Text(text = "扫描完成")
                Text(text = "共扫描文件夹：${scanState.scannedDirCount}")
                Text(text = "共扫描谱面：${scanState.scannedMapCount}")
                Text(text = "扫描错误数：${scanState.playlistScanList.map { it.value.errorStates.count() }.sum()}")
            } else {
                Text(text = "扫描中，请稍后 ${scanState.scannedDirCount}/${scanState.totalDirCount}")
                Text(text = "当前歌单：${scanState.currentPlaylistDir}")
                Text(text = "当前谱面：${scanState.currentMapDir}", maxLines = 1)
            }

            LazyColumn {
                items(scanState.playlistScanList.size) { index ->
                    val item = scanState.playlistScanList.reversed()[index]

                    item.value.let { playlistScanState ->
                        var animatedVisibility by remember { mutableStateOf(false) }
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = playlistScanState.playlistName)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = "${playlistScanState.scannedFileAmount}/${playlistScanState.fileAmount}")

                            if (playlistScanState.errorStates.isNotEmpty()) {
                                IconButton(
                                    onClick = { animatedVisibility = !animatedVisibility },
                                ) {
                                    Icon(Icons.Filled.Error, contentDescription = null)
                                }
                            }
                        }
                        AnimatedVisibility(animatedVisibility) {
                            if (playlistScanState.errorStates.isNotEmpty()) {
                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .weight(1f, fill = false),
                                    horizontalArrangement = Arrangement.End,
                                ) {
                                    Column {
                                        playlistScanState.errorStates.map {
                                            Row(
                                                Modifier.horizontalScroll(rememberScrollState()),
                                            ) {
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
