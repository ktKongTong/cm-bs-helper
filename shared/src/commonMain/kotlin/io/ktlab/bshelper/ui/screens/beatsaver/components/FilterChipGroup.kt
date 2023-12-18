package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FilterChipGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit = {},
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        options.map { text ->
            FilterChip(
                elevation = null,
                modifier = Modifier,
                selected = (text == selectedOption),
                onClick = { onOptionSelected(text) },
                label = { Text(text) },
            )
        }
    }
}
