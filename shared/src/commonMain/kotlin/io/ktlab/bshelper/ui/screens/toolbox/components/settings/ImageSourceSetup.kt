package io.ktlab.bshelper.ui.screens.toolbox.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.ImageSource
import io.ktlab.bshelper.ui.LocalUIEventHandler
import io.ktlab.bshelper.ui.LocalUserPreference
import io.ktlab.bshelper.ui.components.ChipDropDownSelector
import io.ktlab.bshelper.ui.event.ToolboxUIEvent

@Composable
fun ImageSourceSetup() {
    val userPreference = LocalUserPreference.current
    val onUIEvent = LocalUIEventHandler.current
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp,8.dp)
            .clip(MaterialTheme.shapes.medium)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
        ){
            Text("修改图片源", style = MaterialTheme.typography.titleLarge)
            Text("在国内网络条件下，bs图片源可用性不高，使用代理图片源可能有帮助（目前可用性也不高）", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.weight(1f,false))
        val list = listOf(ImageSource.BS, ImageSource.PROXY).map { it.value }
        ChipDropDownSelector(
            options = list,
            selectedOption = if (userPreference.imageSource == ImageSource.BS) list[0] else list[1],
            onSelectedOptionChange = { onUIEvent(ToolboxUIEvent.UpdateImageSource(ImageSource.fromValue(it))) },
        )
    }
}