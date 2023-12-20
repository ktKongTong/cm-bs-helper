package io.ktlab.bshelper.ui.screens.toolbox

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.ui.components.EmptyContent
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.toolbox.components.DownloadTaskCard
import io.ktlab.bshelper.ui.event.ToolboxUIEvent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DownloadTaskScreen(
    onUIEvent: (UIEvent) -> Unit,
    downloadTasks: List<IDownloadTask>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        stickyHeader {
            // order query key
            Surface(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "下载任务", style = MaterialTheme.typography.headlineLarge)
                    IconButton(onClick = {
                        onUIEvent(ToolboxUIEvent.DeleteAllDownloadTasks)
                    }) {
                        Icon(Icons.Rounded.Delete, contentDescription = null)
                    }
                }
            }
        }
        if (downloadTasks.isEmpty()) {
            item {
                EmptyContent()
            }
        }
        items(downloadTasks.size) {
            key(it) {
                DownloadTaskCard(
                    downloadTask = downloadTasks[it],
                    onUIEvent = onUIEvent,
                )
            }
        }
    }
}
