package io.ktlab.bshelper.ui.screens.toolbox

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.BuildConfig
import io.ktlab.bshelper.ui.LocalUserPreference
import io.ktlab.bshelper.ui.components.AppDialog
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.components.DirectoryChooser
import io.ktlab.bshelper.ui.viewmodel.ToolboxUIEvent

@Composable
fun SettingScreen(
    onUIEvent: (UIEvent) -> Unit,
) {

    val userPreference = LocalUserPreference.current
   Column {
       Row {
          Text("设置", style = MaterialTheme.typography.headlineLarge)
       }
       Row(
           Modifier.fillMaxWidth(),
           verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
       ) {
            Text("修改主题色", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.weight(1f,false))
           val openState = remember { mutableStateOf(false) }
            IconButton(onClick = {
                openState.value = true
            }) {
                Canvas(
                    modifier = Modifier
                        .size(24.dp)
                        .border(
                            shape = CircleShape,
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    onDraw = { drawCircle(color = Color(userPreference.themeColor)) },
                )
            }
           ColorPicker(onUIEvent = onUIEvent, openState = openState)
       }
       Row(
           Modifier.fillMaxWidth(),
           verticalAlignment = Alignment.CenterVertically,
           horizontalArrangement = Arrangement.SpaceBetween,
       ) {
           Text("修改管理目录", style = MaterialTheme.typography.titleLarge)
           Spacer(Modifier.weight(1f,false))
           DirectoryChooser(
               targetPath = userPreference.currentManageDir,
               onSelectTargetPath = { path ->
                   onUIEvent(ToolboxUIEvent.UpdateManageDir(path))
               },
               onUIEvent = onUIEvent,
           )
       }
   }
}

@Composable
fun ColorPicker(
    onUIEvent: (UIEvent) -> Unit,
    openState:MutableState<Boolean>,
){
    val colors = remember{
        listOf(
            BuildConfig.THEME_COLOR,
            0xFFFF0000,
            0xFF00FF00,
            0xFF0000FF,
            0xFFFFFF00,
            0xFF00FFFF,
            0xFFFF00FF,
        )
    }
    AppDialog(
        title = "选择颜色",
        openState = openState,
    ){
        // 4 items per row
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            colors.chunked(4).forEach { rowColors ->
                Row {
                    rowColors.forEach { color ->
                        IconButton(
                            onClick = {
                            onUIEvent(ToolboxUIEvent.UpdateThemeColor(color))
                        }) {
                            Canvas(
                                modifier = Modifier
                                    .size(24.dp)
                                    .border(
                                        shape = CircleShape,
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                    ),
                                onDraw = { drawCircle(color = Color(color)) },
                            )
                        }
                    }
                }
            }
        }
    }
}