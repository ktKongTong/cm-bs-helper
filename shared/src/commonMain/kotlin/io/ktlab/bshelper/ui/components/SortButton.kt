package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WidthNormal
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Square
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.WidthNormal
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.model.enums.SortKey
import io.ktlab.bshelper.model.enums.SortType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortButton(
    modifier: Modifier = Modifier,
    sortRule: Pair<SortKey, SortType>,
    onChangeMapListSortRule: (Pair<SortKey, SortType>) -> Unit,
) {
    val sortKey = sortRule.first
    val sortType = sortRule.second
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier.padding(end = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(
                onClick = { expanded = true },
                modifier = Modifier.padding(end = 2.dp),
            ) {
                Row {
                    Icon(Icons.Default.Sort, contentDescription = stringResource(MR.strings.sort))
                    Text(
                        text = sortKey.toString(),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            IconButton(onClick = { onChangeMapListSortRule(sortKey to sortType.reverse()) }) {
                Icon(
                    if (sortType == SortType.ASC) {
                        Icons.Default.ArrowUpward
                    } else {
                        Icons.Default.ArrowDownward
                    },
                    contentDescription = stringResource(MR.strings.sort),
                )
            }
        }

        MaterialTheme(shapes = MaterialTheme.shapes.copy(small = RoundedCornerShape(10.dp))) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(8.dp, (-12).dp),
                modifier = Modifier,
            ) {
                val icons =
                    listOf(
                        Icons.Rounded.WidthNormal,
                        Icons.Rounded.Timer,
                        Icons.Rounded.Speed,
                        Icons.Rounded.Square,
                    )
                SortKey.allSortKeys.forEachIndexed { index, it ->
                    DropdownMenuItem(
                        text = { Text(text = it.toString()) },
                        onClick = {
                            expanded = false
                            onChangeMapListSortRule(it to sortType)
                        },
                        leadingIcon = { Icon(icons[index], contentDescription = it.slug) },
                    )
                }
            }
        }
    }
}

