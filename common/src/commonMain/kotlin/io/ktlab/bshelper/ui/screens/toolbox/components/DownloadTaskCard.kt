package io.ktlab.bshelper.ui.screens.toolbox.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.repository.DownloadTaskType
import io.ktlab.bshelper.repository.IDownloadTask
import io.ktlab.bshelper.ui.components.AsyncImageWithFallback
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.kown.model.TaskStatus


@Composable
fun DownloadTaskCard(
    downloadTask: IDownloadTask,
    modifier: Modifier = Modifier,
    onUIEvent: (UIEvent) -> Unit,
){
    when (downloadTask.taskType) {
        DownloadTaskType.Batch ->{
            BatchMapDownloadTaskCard(
                downloadTask = downloadTask,
                modifier = modifier,
                onUIEvent = onUIEvent,
            )
        }
        DownloadTaskType.Playlist->{

        }
        DownloadTaskType.Map -> {
            MapDownloadTaskCard(
                downloadTask = downloadTask,
                modifier = modifier,
                onUIEvent = onUIEvent,
            )
        }
    }
}