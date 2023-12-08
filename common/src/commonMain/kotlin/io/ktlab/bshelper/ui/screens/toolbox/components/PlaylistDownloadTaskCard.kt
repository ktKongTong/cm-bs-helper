package io.ktlab.bshelper.ui.screens.toolbox.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.repository.IDownloadTask
import io.ktlab.bshelper.ui.components.AsyncImageWithFallback
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.ToolboxUIEvent
import io.ktlab.kown.model.*
import kotlinx.datetime.Instant

@Composable
fun PlaylistDownloadTaskCard(
    downloadTask: IDownloadTask,
    modifier: Modifier = Modifier,
    onUIEvent: (UIEvent) -> Unit,
){
    val map = downloadTask as IDownloadTask.PlaylistDownloadTask
    var expanded by remember { mutableStateOf(false) }
    Column {
        val finiteCount = map.taskList.count { it.downloadTaskModel.status.isFinite() }
        val succeedCount = map.taskList.count { it.downloadTaskModel.status.isSuccessFinite() }
        val processingCount = map.taskList.count { it.downloadTaskModel.status.isProcessing() || it.downloadTaskModel.status.isWaiting() }
        val waitingCount = map.taskList.count { it.downloadTaskModel.status.isWaiting() }
        val pausedCount = map.taskList.count { it.downloadTaskModel.status is KownTaskStatus.Paused }
        val totalCount = map.taskList.size
        Row(
            modifier = modifier
                .fillMaxWidth()
        ) {
            AsyncImageWithFallback(
                modifier = Modifier
                    .padding(8.dp)
                    .size(128.dp, 128.dp)
                    .align(Alignment.CenterVertically)
                    .clip(shape = RoundedCornerShape(10.dp)),
                source = map.playlist.getAvatar(),
            )
            // take two task title
            val title = map.playlist.title
            Column (
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ){
                Text(text = title,
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier
                        .widthIn(0.dp, 400.dp)
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                ) {
                    if (finiteCount == totalCount) {
                        Text(text = "已完成，成功：${succeedCount}/${totalCount}")
                    }
                    if (finiteCount < totalCount) {
                        Text(text = "下载中，：${totalCount-processingCount}/${totalCount}")
                    }

                }
                Text(text = "creatAt：${map.taskList.firstOrNull()?.let  { Instant.fromEpochMilliseconds(it.downloadTaskModel.createdAt).toString() }}")
            }
            Row(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // start paused task
                if (pausedCount > 0) {
                    IconButton(onClick = { onUIEvent(ToolboxUIEvent.ResumeDownloadTask(downloadTask)) }) {
                        Icon(
                            Icons.Rounded.PlayArrow,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                if (finiteCount == totalCount && succeedCount != finiteCount) {
                    IconButton(onClick = { onUIEvent(ToolboxUIEvent.RetryDownloadMap(downloadTask)) }) {
                        Icon(
                            Icons.Rounded.Redo,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                if (finiteCount < totalCount) {
                    IconButton(onClick = { onUIEvent(ToolboxUIEvent.PauseDownloadTask(downloadTask)) }) {
                        Icon(
                            Icons.Rounded.Pause,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { onUIEvent(ToolboxUIEvent.CancelDownloadTask(downloadTask)) }) {
                        Icon(
                            Icons.Rounded.Cancel,
                            contentDescription = null
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if(expanded) {
                            Icons.Rounded.ExpandLess}else {
                            Icons.Rounded.ExpandMore},
                        contentDescription = null
                    )
                }
            }
        }
    }

    AnimatedVisibility(visible = expanded) {
//            TODO max size should be 10, if more than 10, able to scroll ?
        Column {
            map.taskList.forEach {
                MapDownloadTaskCard(
                    downloadTask = it,
                    modifier = modifier.padding(start = 16.dp),
                    onUIEvent = onUIEvent,
                )
            }
        }
    }
}