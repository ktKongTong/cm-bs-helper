package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
                        Icons.Default.WidthNormal,
                        Icons.Default.Timer,
                        Icons.Default.Speed,
                        Icons.Default.Square,
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

@Composable
fun SortButtonPreview() {
//    SortButton(sortRule = SortKey.NPS to SortType.ASC, onChangeMapListSortRule = {})
    Text("Hello")
    Box(Modifier.clip(RoundedCornerShape(10.dp))) {
//        ExposedDropdownMenuBox()
        DropdownMenu(
            expanded = true,
            onDismissRequest = { },
            offset = DpOffset(8.dp, (-12).dp),
            properties = PopupProperties(focusable = false),
            modifier = Modifier,
        ) {
            val icons =
                listOf(
                    Icons.Default.WidthNormal,
                    Icons.Default.Timer,
                    Icons.Default.Speed,
                    Icons.Default.Square,
                )
            SortKey.allSortKeys.forEachIndexed { index, it ->
                DropdownMenuItem(
                    text = { Text(text = it.toString()) },
                    onClick = {},
                    leadingIcon = { Icon(icons[index], contentDescription = it.slug) },
                )
            }
        }
    }
}
