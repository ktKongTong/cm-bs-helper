package io.ktlab.bshelper.ui.screens.toolbox.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.SManageFolder
import io.ktlab.bshelper.ui.LocalUIEventHandler
import io.ktlab.bshelper.ui.LocalUserPreference
import io.ktlab.bshelper.ui.components.ChipDropDownSelectorV2
import io.ktlab.bshelper.ui.event.GlobalUIEvent
import io.ktlab.bshelper.ui.screens.toolbox.components.ManageFolderDialog

@Composable
fun ManageFolderSetup(
    manageFolders: List<SManageFolder>,
) {
    val userPreference = LocalUserPreference.current
    val onUIEvent = LocalUIEventHandler.current
    if (userPreference.currentManageFolder!=null) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp,8.dp)
                .clip(MaterialTheme.shapes.medium)
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("修改管理目录", style = MaterialTheme.typography.titleLarge)
                Text("会暂停当前管理目录下的所有下载任务", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.weight(1f,false))
            ChipDropDownSelectorV2(
                modifier = Modifier.widthIn(min=100.dp),
                options = manageFolders,
                selectedOption =  userPreference.currentManageFolder,
                showText = { it.name + "(${it.gameType.human})" },
                onSelectedOptionChange = {
                    onUIEvent(GlobalUIEvent.UpdateManageFolder(it))
                }
            )
        }
    }

    val openState = remember { mutableStateOf(false) }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp,8.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = { openState.value = true }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("删除管理目录", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.weight(1f,false))
        Icon(
            imageVector = Icons.Rounded.ArrowRight,
            contentDescription = "Arrow Right",
            modifier = Modifier.padding(16.dp)
        )
    }
    ManageFolderDialog(
        openState = openState,
        manageFolders = manageFolders,
        onUIEvent = onUIEvent
    )
}