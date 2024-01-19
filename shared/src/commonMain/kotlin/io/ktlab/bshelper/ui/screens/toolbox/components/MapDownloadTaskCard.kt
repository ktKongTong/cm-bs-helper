package io.ktlab.bshelper.ui.screens.toolbox.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Start
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.model.enums.MapTag
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.ui.components.AsyncImageWithFallback
import io.ktlab.bshelper.ui.components.DurationIconWIthText
import io.ktlab.bshelper.ui.components.MapItemV3
import io.ktlab.bshelper.ui.components.MapTag
import io.ktlab.bshelper.ui.components.MapperIconWIthText
import io.ktlab.bshelper.ui.components.NPSIconWIthText
import io.ktlab.bshelper.ui.components.ThumbDownIconWIthText
import io.ktlab.bshelper.ui.components.ThumbUpIconWIthText
import io.ktlab.bshelper.ui.components.labels.DateLabel
import io.ktlab.bshelper.ui.components.labels.MapDiffLabel
import io.ktlab.bshelper.ui.event.ToolboxUIEvent
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.beatsaver.components.DownloadIconButton
import io.ktlab.bshelper.utils.format
import io.ktlab.kown.model.KownTaskStatus
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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
    val map = (downloadTask as IDownloadTask.MapDownloadTask).relateMap
    Box {
        MapItemV3(
            map = map,
            hoveredActionBar = {
                Row(
                    modifier =
                    Modifier
                        .padding(end = 8.dp)
                        .fillMaxHeight(),
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
        ){
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                when (downloadTask.downloadTaskModel.status) {
                    is KownTaskStatus.Queued -> {
                        Text(text = "等待中")
                    }
                    is KownTaskStatus.Running -> {
                        val animatedProgress by animateFloatAsState(
                            targetValue = downloadTask.getProgress(),
                            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                            label = "",
                        )
                        CircularProgressIndicator(
                            progress = animatedProgress,
                            strokeCap = StrokeCap.Round,
                            modifier = Modifier.size(24.dp),
                        )
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
                        Text(text = "解压至歌单中")
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
        Row (
            modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
        ){


            Text(
                text = "目标歌单：${downloadTask.targetPlaylist.getName()}",
                modifier = Modifier.padding(bottom = 4.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
