package io.ktlab.bshelper.ui.screens.toolbox

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.repository.IDownloadTask
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
                Column {
                    TextButton(onClick = {
                        onUIEvent(ToolboxUIEvent.DeleteAllDownloadTasks)
                    }) {
                        Text(text = "delete all history tasks")
                    }
                }
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