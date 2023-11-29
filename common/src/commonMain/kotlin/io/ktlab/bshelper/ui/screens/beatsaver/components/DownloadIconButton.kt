package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.repository.IDownloadTask
import io.ktlab.kown.model.KownTaskStatus

@Composable
fun DownloadIconButton(
    downloadInfo: IDownloadTask.MapDownloadTask?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.size(36.dp)
){
    if (downloadInfo != null) {
        when(downloadInfo.downloadTaskModel.status) {
            is KownTaskStatus.Running,is KownTaskStatus.Queued,is KownTaskStatus.PostProcessing -> {
                val animatedProgress by animateFloatAsState(
                    targetValue = downloadInfo.getProgress(),
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                    label = ""
                )
                CircularProgressIndicator(
                    progress = animatedProgress,
                    strokeCap = StrokeCap.Round,
                    modifier = modifier
                )
            }
            is KownTaskStatus.Completed -> {
                Icon(
                    Icons.Rounded.Check,
                    contentDescription = "Download complete",
                    modifier = modifier
                )
            }

        }

    }else {
        IconButton(onClick = onClick,
            modifier = modifier){
            Icon(
                Icons.Rounded.Download,
                contentDescription = "Download Map",
            )
        }
    }

}