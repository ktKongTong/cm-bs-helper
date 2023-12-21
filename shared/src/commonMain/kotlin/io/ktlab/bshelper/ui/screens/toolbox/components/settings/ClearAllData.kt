package io.ktlab.bshelper.ui.screens.toolbox.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.ui.LocalUIEventHandler
import io.ktlab.bshelper.ui.LocalUserPreference
import io.ktlab.bshelper.ui.components.AppDialog
import io.ktlab.bshelper.ui.event.GlobalUIEvent

@Composable
fun ClearAllData() {
    val userPreference = LocalUserPreference.current
    val onUIEvent = LocalUIEventHandler.current
    val openState = remember { mutableStateOf(false) }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp,8.dp)
            .clip(MaterialTheme.shapes.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text("删除所有本地数据", style = MaterialTheme.typography.titleLarge)
            Text(
                "删除所有本地数据，如下载任务，曲包数据等，不会对本地文件进行修改",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.weight(1f, false))

        TextButton(
            onClick = {
                openState.value = true
            }
        ) {
            Text("删除")
        }
    }
    AppDialog(
        "删除",
        "确定要删除所有本地数据吗？",
        openState = openState,
        onCancel = {},
        onConfirm = {
            onUIEvent(GlobalUIEvent.ClearAllData)
        }
    )
}