package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun RadioButtons(
    radioOptions : List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit = {},
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .padding(4.dp)
                    .onClick { onOptionSelected(text) },
                horizontalArrangement = Arrangement.Center,
            ) {
                RadioButton(
                    modifier = Modifier.clip(RoundedCornerShape(4.dp)),
                    selected = (text == selectedOption),
                    onClick = { onOptionSelected(text) },
                )
                Text(
                    text = text,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
        }
    }
}
