package io.ktlab.bshelper.ui.screens.toolbox

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.ui.components.EmptyContent
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.toolbox.components.DownloadTaskCard
import io.ktlab.bshelper.viewmodel.ToolboxUIEvent


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DownloadTaskScreen(
    onUIEvent: (UIEvent) -> Unit,
    downloadTasks: List<IDownloadTask>,
){
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ){
        stickyHeader {
            // order query key
            Surface(
                modifier = Modifier.fillMaxWidth(),
            ) {

                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                ){
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
//        val sortedDownloadTasks = downloadTasks
        items(downloadTasks.size){
            // 对象没变化，不会重新渲染
            // 如何强制渲染？
            val task = downloadTasks[it]
            key(it) {
                DownloadTaskCard(
                    downloadTask = downloadTasks[it],
                    onUIEvent = onUIEvent,
                )
            }

        }
    }
}