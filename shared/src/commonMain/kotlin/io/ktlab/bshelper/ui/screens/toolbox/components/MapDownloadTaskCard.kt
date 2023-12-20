package io.ktlab.bshelper.ui.screens.toolbox.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Start
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.model.enums.MapTag
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.ui.components.*
import io.ktlab.bshelper.ui.components.labels.MapDiffLabel
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.event.ToolboxUIEvent
import io.ktlab.kown.model.KownTaskStatus
import kotlinx.datetime.Instant

fun Long.toMB(): String {
    // remain 2 decimal
    return String.format("%.2f MB", this / 1024f / 1024f)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MapDownloadTaskCard(
    downloadTask: IDownloadTask,
    modifier: Modifier = Modifier,
    onUIEvent: (UIEvent) -> Unit,
) {
    Column {
        val map = downloadTask as IDownloadTask.MapDownloadTask
        Row(
            modifier =
                modifier
                    .fillMaxWidth(),
        ) {
            AsyncImageWithFallback(
                modifier =
                    Modifier
                        .padding(8.dp)
                        .size(128.dp, 128.dp)
                        .align(Alignment.CenterVertically)
                        .clip(shape = RoundedCornerShape(10.dp)),
                source = map.relateMap.getAvatar(),
            )
            Column(
                modifier =
                    Modifier
                        .weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = map.relateMap.getSongName(),
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row {
                    if ((map.relateMap as BSMapVO).uploader.verifiedMapper?.let { true } == true) {
                        Icon(
                            Icons.Filled.Verified,
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Verified Mapper",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    MapperIconWIthText(text = map.relateMap.getAuthor())
                    Spacer(modifier = Modifier.width(8.dp))
                    DurationIconWIthText(text = map.relateMap.getDuration())
                    Spacer(modifier = Modifier.width(8.dp))
                    NPSIconWIthText(text = "%.2f".format(map.relateMap.getMaxNPS()))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FlowRow {
                        MapTag.sort((map.relateMap as BSMapVO).map.tags).map {
                            MapTag(tag = it)
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row {
                        ThumbUpIconWIthText(text = (map.relateMap as BSMapVO).map.upVotes.toString())
                        Spacer(modifier = Modifier.width(8.dp))
                        ThumbDownIconWIthText(text = (map.relateMap as BSMapVO).map.downVotes.toString())
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    MapDiffLabel(diff = (map.relateMap as BSMapVO).getDiffMatrix())
                }
                Text(
                    text = "目标歌单：${map.targetPlaylist.getName()}",
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(text = "creatAt：${Instant.fromEpochMilliseconds(map.downloadTaskModel.createdAt)}")
                Row(
                    modifier =
                        Modifier
                            .widthIn(0.dp, 400.dp)
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                ) {
                    when (downloadTask.downloadTaskModel.status) {
                        is KownTaskStatus.Queued -> {
                            Text(text = "等待中")
                        }
                        is KownTaskStatus.Running -> {
                            Text(
                                text = "${downloadTask.downloadTaskModel
                                    .downloadedBytes.toMB()}/${downloadTask.downloadTaskModel
                                    .totalBytes.toMB()}",
                            )
                        }
                        is KownTaskStatus.Completed -> {
                            Text(text = "下载完成")
                        }
                        is KownTaskStatus.PostProcessing -> {
                            Text(text = "解压至歌单")
                        }
                        is KownTaskStatus.Failed -> {
                            Text(
                                text = "下载失败：${(downloadTask.downloadTaskModel.status as KownTaskStatus.Failed).reason}",
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                softWrap = false,
                            )
                        }
                    }
                }
            }
            Row(
                modifier =
                    Modifier
                        .padding(end = 8.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when (downloadTask.downloadTaskModel.status) {
                    is KownTaskStatus.Queued, is KownTaskStatus.Running -> {
                        IconButton(onClick = { onUIEvent(ToolboxUIEvent.PauseDownloadTask(downloadTask)) }) {
                            Icon(
                                Icons.Rounded.Pause,
                                contentDescription = null,
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { onUIEvent(ToolboxUIEvent.CancelDownloadTask(downloadTask)) }) {
                            Icon(
                                Icons.Rounded.Cancel,
                                contentDescription = null,
                            )
                        }
                    }
                    is KownTaskStatus.Paused -> {
                        IconButton(onClick = { onUIEvent(ToolboxUIEvent.ResumeDownloadTask(downloadTask)) }) {
                            Icon(
                                Icons.Rounded.Start,
                                contentDescription = null,
                            )
                        }
                    }
                    is KownTaskStatus.Failed -> {
                        IconButton(onClick = { onUIEvent(ToolboxUIEvent.RetryDownloadMap(downloadTask)) }) {
                            Icon(
                                Icons.Rounded.Redo,
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        }
    }
}
