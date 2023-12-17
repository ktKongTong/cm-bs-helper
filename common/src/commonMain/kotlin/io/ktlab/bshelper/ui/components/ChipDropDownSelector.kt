package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipDropDownSelector(
    options: List<String>,
    selectedOption: String,
    modifier: Modifier = Modifier,
    onSelectedOptionChange: (String) -> Unit,
){
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        FilterChip(
            elevation = null,
            modifier = Modifier.menuAnchor(),
            label = { Text(selectedOption) },
            selected = false,
            onClick = {expanded = true},
//            leadingIcon = {Icon(Icons.Rounded.Check, "Checked Icon")},
            trailingIcon = { Icon(Icons.Rounded.ExpandMore,"ExpandMore Icon") },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(0.dp, 250.dp)
                .verticalScroll(rememberScrollState())
        ){
            options.map {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        expanded = false
                        onSelectedOptionChange(it)
                    }
                )
            }
        }
    }
}