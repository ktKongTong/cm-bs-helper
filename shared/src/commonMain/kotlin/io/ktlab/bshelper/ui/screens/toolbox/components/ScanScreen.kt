package io.ktlab.bshelper.ui.screens.toolbox.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.SManageFolder
import io.ktlab.bshelper.model.enums.GameType
import io.ktlab.bshelper.model.scanner.ScanStateEventEnum
import io.ktlab.bshelper.model.scanner.ScanStateV2
import io.ktlab.bshelper.ui.LocalUserPreference
import io.ktlab.bshelper.ui.components.ChipDropDownSelector
import io.ktlab.bshelper.ui.components.DirectoryChooser
import io.ktlab.bshelper.ui.composables.RequestStoragePermission
import io.ktlab.bshelper.ui.composables.isStoragePermissionGranted
import io.ktlab.bshelper.ui.event.GlobalUIEvent
import io.ktlab.bshelper.ui.event.ToolboxUIEvent
import io.ktlab.bshelper.ui.event.UIEvent
import okio.Path.Companion.toPath




@Composable
fun StepContent(
    scanState: ScanStateV2,
    targetPath: String,
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
