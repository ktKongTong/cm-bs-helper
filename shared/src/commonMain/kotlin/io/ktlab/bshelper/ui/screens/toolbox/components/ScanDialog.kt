package io.ktlab.bshelper.ui.screens.toolbox.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import io.ktlab.bshelper.model.enums.GameType
import io.ktlab.bshelper.model.scanner.ScanStateEventEnum
import io.ktlab.bshelper.model.scanner.ScanStateV2
import io.ktlab.bshelper.ui.components.ChipDropDownSelector
import io.ktlab.bshelper.ui.components.DirectoryChooser
import io.ktlab.bshelper.ui.composables.RequestStoragePermission
import io.ktlab.bshelper.ui.composables.isStoragePermissionGranted
import io.ktlab.bshelper.ui.event.GlobalUIEvent
import io.ktlab.bshelper.ui.event.ToolboxUIEvent
import io.ktlab.bshelper.ui.event.UIEvent
import okio.Path.Companion.toPath

@Composable
fun ScanDialog(
    openState:MutableState<Boolean>,
    scanStateV2: ScanStateV2,
    onUIEvent: (UIEvent)->Unit,
    manageFolders:List<SManageFolder>
) {
    if (openState.value) {

        RequestStoragePermission()

        var currentGameType by remember { mutableStateOf(GameType.LightBand) }
        var currentManageDir by remember { mutableStateOf("") }
        val isStoragePermissionGranted = isStoragePermissionGranted()
        AlertDialog(
            onDismissRequest = { openState.value = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("扫描曲包", style = MaterialTheme.typography.headlineLarge)
                }
            },
            dismissButton = {
                if(scanStateV2.state == ScanStateEventEnum.NOT_START){
                    TextButton(onClick = {
                        openState.value = false
                    }) {
                        Text(text = "取消")
                    }
                }
            },
            confirmButton = {
                if(scanStateV2.state == ScanStateEventEnum.SCAN_COMPLETE
                    ||
                    scanStateV2.state == ScanStateEventEnum.SCAN_ERROR
                ){
                    TextButton(onClick = {
                        openState.value = false
                        onUIEvent(ToolboxUIEvent.ClearScanState)
                    }) {
                        Text(text = "确定")
                    }
                }else if (scanStateV2.state == ScanStateEventEnum.NOT_START) {
                        TextButton(onClick = {
                            if (!isStoragePermissionGranted) {
                                onUIEvent(GlobalUIEvent.ShowSnackBar("请先授予存储权限"))
                                return@TextButton
                            }
                            onUIEvent(ToolboxUIEvent.ScanPlaylist(currentManageDir,currentGameType))
                        }) {
                            Text(text = "扫描")
                        }
                }
            },
            text = {
                Column {
                var supportingText by remember { mutableStateOf("") }
                DirectoryChooser(
                    targetPath = currentManageDir,
                    enabled = false,
                    onSelectTargetPath = { path ->
                        if(manageFolders.any { path.toPath().toString() == it.path }) {
                            supportingText = "目标目录已存在，要进行扫描请先删除"
                            currentManageDir = ""
                            return@DirectoryChooser
                        }
                        supportingText = ""
                        currentManageDir = path.toPath().toString()
                    },
                    supportingText = {
                        if(supportingText.isNotBlank()) {
                            Text(text = supportingText)
                        }
                    },
                    onUIEvent = onUIEvent,
                )

                val list = listOf(
                    GameType.LightBand,
                    GameType.BeatKungFu,
                    GameType.BeatSaberLike).map { it.human }
                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        Text("游戏类型：")
                        ChipDropDownSelector(
                            options = list,
                            selectedOption = if (currentGameType == GameType.LightBand) list[0] else list[1],
                            onSelectedOptionChange = { currentGameType = GameType.fromHuman(it) },
                            enabled = if(scanStateV2.state == ScanStateEventEnum.NOT_START) true else false,
                        )
                    }
                    Column(
                        modifier =
                        Modifier
                            .padding(horizontal = 32.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Box(
                            modifier =
                            Modifier
                                .fillMaxHeight(0.7f)
                                .weight(1f, fill = false),
                        ) {
                            StepContent(scanStateV2, currentManageDir, onUIEvent)
                        }
                    }
                }
            },
            properties =
            DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
        )
    }
}