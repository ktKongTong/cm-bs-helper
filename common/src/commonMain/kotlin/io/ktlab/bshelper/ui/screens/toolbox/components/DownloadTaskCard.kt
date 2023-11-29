package io.ktlab.bshelper.ui.screens.toolbox.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.ktlab.bshelper.repository.DownloadTaskType
import io.ktlab.bshelper.repository.IDownloadTask
import io.ktlab.bshelper.ui.event.UIEvent


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