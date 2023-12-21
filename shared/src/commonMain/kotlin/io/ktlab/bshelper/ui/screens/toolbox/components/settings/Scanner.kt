package io.ktlab.bshelper.ui.screens.toolbox.components.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import io.ktlab.bshelper.model.scanner.ScanStateV2
import io.ktlab.bshelper.ui.LocalUIEventHandler
import io.ktlab.bshelper.ui.screens.toolbox.components.ScanDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Scanner(
    scanState: ScanStateV2,
    manageFolders:List<SManageFolder>
) {
    val onUIEvent = LocalUIEventHandler.current
    val openState = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(16.dp,8.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .combinedClickable {
                openState.value = true
            }
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("曲包扫描", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.weight(1f,false))
        Icon(
            imageVector = Icons.Rounded.ArrowRight,
            contentDescription = "Direction",
            modifier = Modifier.padding(16.dp)
        )
    }
    ScanDialog(
        openState = openState,
        scanStateV2 = scanState,
        onUIEvent = onUIEvent,
        manageFolders = manageFolders
    )
}