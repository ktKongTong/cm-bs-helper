package io.ktlab.bshelper.ui.screens.toolbox

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.ManageFolderBackup
import io.ktlab.bshelper.model.SManageFolder
import io.ktlab.bshelper.model.scanner.ScanStateV2
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.toolbox.components.settings.BackUp
import io.ktlab.bshelper.ui.screens.toolbox.components.settings.ClearAllData
import io.ktlab.bshelper.ui.screens.toolbox.components.settings.ColorPickerRow
import io.ktlab.bshelper.ui.screens.toolbox.components.settings.DocPage
import io.ktlab.bshelper.ui.screens.toolbox.components.settings.Feedback
import io.ktlab.bshelper.ui.screens.toolbox.components.settings.ImageSourceSetup
import io.ktlab.bshelper.ui.screens.toolbox.components.settings.ManageFolderSetup
import io.ktlab.bshelper.ui.screens.toolbox.components.settings.Scanner

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ToolScreen(
    scanState: ScanStateV2,
    onUIEvent: (UIEvent) -> Unit,
    manageFolders:List<SManageFolder>,
    backups:List<ManageFolderBackup>
) {
    Column (
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
    ) {
        Row { Text("设置", style = MaterialTheme.typography.headlineLarge) }
        ColorPickerRow(onUIEvent)
        ImageSourceSetup()
        Row { Text("目录管理", style = MaterialTheme.typography.headlineLarge) }
        Scanner(scanState,manageFolders)
        ManageFolderSetup(manageFolders)
        ClearAllData()
        Row { Text("其他", style = MaterialTheme.typography.headlineLarge) }
        BackUp(manageFolders,backups)
        Feedback()
        DocPage()

    }
}


