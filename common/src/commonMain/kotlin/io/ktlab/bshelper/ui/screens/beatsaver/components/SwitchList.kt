package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

data class SwitchItem(
    val id: Int,
    val title: String,
    val checked: Boolean,
    val description: String = "",
    val enabled: Boolean = true,
)

@Composable
fun SwitchList(
    modifier: Modifier = Modifier,
    switchModifier: Modifier = Modifier,
    switchList: List<SwitchItem>,
    onSwitchItemCheckedChange: (Int, Boolean) -> Unit = { _, _ -> },
    thumbContent: (@Composable () -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Column(modifier) {
        switchList.mapIndexed { index, switchItem ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = switchItem.title, style = MaterialTheme.typography.labelLarge)
                Switch(
                    modifier = switchModifier,
                    checked = switchItem.checked,
                    onCheckedChange = { onSwitchItemCheckedChange(index, it) },
                    enabled = switchItem.enabled,
                    interactionSource = interactionSource,
                    thumbContent = thumbContent,
                )
            }
        }
    }
}
