package io.ktlab.bshelper.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WidthNormal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import io.ktlab.bshelper.BuildConfig
import io.ktlab.bshelper.model.enums.SortKey
import io.ktlab.bshelper.ui.theme.BSHelperTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BSDateRangePicker(
    modifier: Modifier = Modifier,
    state: DateRangePickerState,
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.background(Color.Red),
    ) {
        val formatter = remember { DatePickerDefaults.dateFormatter() }
        Column {
            DateRangePicker(
                state = state,
                dateFormatter = formatter,
                title = {
                },
                modifier = Modifier,
            )
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = { state.setSelection(null, null) }) {
                    Icon(Icons.Filled.Clear, "Clear Icon")
                    Text(
                        modifier = Modifier.padding(start = 2.dp),
                        text = "清除",
                    )
                }
                TextButton(onClick = {
                    onDismissRequest()
                    onConfirm()
                }) {
                    Icon(Icons.Filled.Check, "Confirm Icon")
                    Text(text = "确定")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MapOnlinePreviewPreview() {
    val dateRangePickerState =
        rememberDateRangePickerState(
            initialSelectedStartDateMillis = 0L,
            initialSelectedEndDateMillis = 57878550L,
            initialDisplayMode = DisplayMode.Input,
        )

        BSHelperTheme(seedColor = Color(BuildConfig.THEME_COLOR)) {
//            Column {
//                Card {
                    BSDateRangePicker(
                        state = dateRangePickerState,
                        title = {},
                    )
//                }
//            }
        }

//    }
}


@Composable
fun SortButtonPreview() {
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

