package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipDropDownSelector(
    options: List<String>,
    selectedOption: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onSelectedOptionChange: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        FilterChip(
            elevation = null,
            modifier = modifier.menuAnchor(),
            label = { Text(selectedOption) },
            selected = false,
            onClick = { if(enabled) expanded = true },
//            leadingIcon = {Icon(Icons.Rounded.Check, "Checked Icon")},
            trailingIcon = { Icon(Icons.Rounded.ExpandMore, "ExpandMore Icon") },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier =
                Modifier
                    .heightIn(0.dp, 250.dp)
                    .verticalScroll(rememberScrollState()),
        ) {
            options.map {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        expanded = false
                        onSelectedOptionChange(it)
                    },
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun<T> ChipDropDownSelectorV2(
    options: List<T>,
    selectedOption: T,
    modifier: Modifier = Modifier,
    showText: (T) -> String,
    onSelectedOptionChange: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        FilterChip(
            elevation = null,
            modifier = modifier.menuAnchor(),
            label = { Text(showText(selectedOption)) },
            selected = false,
            onClick = { expanded = true },
//            leadingIcon = {Icon(Icons.Rounded.Check, "Checked Icon")},
            trailingIcon = { Icon(Icons.Rounded.ExpandMore, "ExpandMore Icon") },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier =
            Modifier
                .heightIn(0.dp, 250.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            options.map {
                DropdownMenuItem(
                    text = { Text(showText(it)) },
                    onClick = {
                        expanded = false
                        onSelectedOptionChange(it)
                    },
                )
            }
        }
    }
}