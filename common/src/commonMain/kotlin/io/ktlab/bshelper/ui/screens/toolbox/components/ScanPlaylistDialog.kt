package io.ktlab.bshelper.ui.screens.toolbox.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.model.vo.GlobalScanStateEnum
import io.ktlab.bshelper.model.vo.PlaylistScanState
import io.ktlab.bshelper.model.vo.PlaylistScanStateEnum
import io.ktlab.bshelper.model.vo.ScanState
import io.ktlab.bshelper.model.vo.ScanStateEnum
import io.ktlab.bshelper.ui.components.CancelButton
import io.ktlab.bshelper.ui.components.ConfirmButton
import io.ktlab.bshelper.ui.components.NextStepIconButton
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.ToolboxUIEvent

private fun getStepByCurrentState(state:GlobalScanStateEnum):Int {
    return when(state) {
        GlobalScanStateEnum.NOT_START -> 0
        GlobalScanStateEnum.SCANNING_PLAYLISTS -> 1
        GlobalScanStateEnum.SCAN_PLAYLISTS_COMPLETE -> 2
        GlobalScanStateEnum.SCANNING_MAPS -> 3
        GlobalScanStateEnum.SCAN_COMPLETE -> 4
        GlobalScanStateEnum.SCAN_ERROR -> 4
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanPlaylistDialog(
    scanState: ScanState,
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
                .padding(16.dp)
                ,
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
                StepsProgressBar(numberOfSteps = 4, currentStep = getStepByCurrentState(scanState.state))
                var targetPath by remember { mutableStateOf("") }
                Box(modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .weight(1f, fill = false)
                ) {
                    StepContent(scanState,targetPath,{targetPath = it},onUIEvent)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    when (scanState.state) {
                        GlobalScanStateEnum.NOT_START -> {
                            CancelButton { onCloseDialog() }
                            NextStepIconButton { onUIEvent(ToolboxUIEvent.ScanPlaylistTapped(targetPath)) }
                        }
                        GlobalScanStateEnum.SCAN_PLAYLISTS_COMPLETE -> {
                            CancelButton { onCloseDialog() }
                            ConfirmButton { onUIEvent(ToolboxUIEvent.ScanSelectedPlaylist) }
                        }
                        GlobalScanStateEnum.SCANNING_MAPS,GlobalScanStateEnum.SCANNING_PLAYLISTS -> {
                            CancelButton {  }
                        }
                        GlobalScanStateEnum.SCAN_COMPLETE,GlobalScanStateEnum.SCAN_ERROR -> {
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
    scanState: ScanState,
    targetPath: String,
    onSelectTargetPath: (String) -> Unit,
    onUIEvent: (UIEvent) -> Unit
){
    var visible by remember { mutableStateOf(true) }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ){
        when(scanState.state) {
            GlobalScanStateEnum.NOT_START -> {
                DirectoryChooser(targetPath,onSelectTargetPath,onUIEvent)
            }
            GlobalScanStateEnum.SCANNING_PLAYLISTS -> {
                Row {
                    Icon(Icons.Filled.AllInclusive, contentDescription = null)
                    Text(text = "扫描中，请稍后")
                }
            }
            GlobalScanStateEnum.SCAN_PLAYLISTS_COMPLETE -> {
                LazyColumn {
                    items(scanState.playlistStates.size) { index ->
                        val item = scanState.playlistStates[index].collectAsState()
                        ScanPlaylistCard(
                            globalScanStateEnum = scanState.state,
                            playlistScanState = item.value,
                            onUIEvent = onUIEvent,
                        )
                    }
                }
            }
            GlobalScanStateEnum.SCANNING_MAPS -> {
                LazyColumn {
                    items(scanState.playlistStates.size) { index ->
                        val item = scanState.playlistStates[index].collectAsState()
                        ScanPlaylistCard(
                            globalScanStateEnum = scanState.state,
                            playlistScanState = item.value,
                            onUIEvent = onUIEvent,
                        )
                    }
                }
            }
            GlobalScanStateEnum.SCAN_COMPLETE -> {
                Text(
    //                modifier = Modifier.align(Alignment.Center),
                    text = "扫描完成"
                )
            }
            GlobalScanStateEnum.SCAN_ERROR -> {
                Text(text = "Error: ${scanState.error?.message}")
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
    BSDirectoryPicker(showDirPicker){ path ->
        showDirPicker = false
        if (path != null) {
            onUIEvent(ToolboxUIEvent.UpdateDefaultManageDir(path))
            onSelectTargetPath(path)
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
                onUIEvent(ToolboxUIEvent.SelectPlaylistTobeScan(playlistScanState))
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