package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.kown.model.KownTaskStatus
import io.ktlab.kown.model.isProcessing

@Composable
fun DownloadIconButton(
    downloadInfo: IDownloadTask.MapDownloadTask?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.size(24.dp),
) {
    if(downloadInfo != null && !downloadInfo.downloadTaskModel.status.isProcessing()) {
        when (downloadInfo.downloadTaskModel.status) {
            is KownTaskStatus.Running, is KownTaskStatus.Queued, is KownTaskStatus.PostProcessing -> {
                val animatedProgress by animateFloatAsState(
                    targetValue = downloadInfo.getProgress(),
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                    label = "",
                )
                CircularProgressIndicator(
                    progress = animatedProgress,
                    strokeCap = StrokeCap.Round,
                    modifier = modifier,
                )
            }
        }
    } else {
        IconButton(
            onClick = onClick,
            modifier = modifier,
        ) {
            Icon(
                Icons.Rounded.Download,
                contentDescription = "Download Map",
            )
        }
    }
}

@Composable
fun PlaylistDownloadIconButton(
    downloadInfo: IDownloadTask.PlaylistDownloadTask?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.size(24.dp),
) {
    if (downloadInfo != null) {
        val progress = downloadInfo.taskList.count { it.downloadTaskModel.status.isProcessing() } / downloadInfo.taskList.count() * 1.0f
        if (progress != 1f) {
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                label = "",
            )
            CircularProgressIndicator(
                progress = animatedProgress,
                strokeCap = StrokeCap.Round,
                modifier = modifier,
            )
        } else {
            Icon(
                Icons.Rounded.Check,
                contentDescription = "Download complete",
                modifier = modifier,
            )
        }
    } else {
        IconButton(
            onClick = onClick,
            modifier = modifier,
        ) {
            Icon(
                Icons.Rounded.Download,
                contentDescription = "Download Map",
            )
        }
    }
}
