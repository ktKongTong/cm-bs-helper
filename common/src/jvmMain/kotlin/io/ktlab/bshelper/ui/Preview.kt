package io.ktlab.bshelper.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.ui.theme.BSHelperTheme

//fun dateStringToLong(dateString: String?): Long? {
//    return if ((dateString == null) or (dateString == "Start Date") or (dateString == "End Date")) { null } else {
//        val sdf = SimpleDateFormat("yyyy-MM-dd")
//        sdf.parse(dateString)!!.time
//    }
//}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BSDateRangePicker(
    modifier: Modifier = Modifier,
    state: DateRangePickerState,
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        DateRangePicker(
            state = state,
            dateFormatter = DatePickerFormatter("yyyy-MM-dd"),
            title = {

            },
            modifier = Modifier,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = { state.setSelection(null, null) }) {
                Icon(Icons.Filled.Clear, "Clear Icon")
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = "清除"
                )
            }
            TextButton(onClick = { onDismissRequest();onConfirm() }) {
                Icon(Icons.Filled.Check, "Confirm Icon")
                Text(text = "确定")
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MapOnlinePreviewPreview() {
//    val dateRangePickerState = rememberDateRangePickerState(
//        initialSelectedStartDateMillis = dateStringToLong("2023-05-12"),
//        initialSelectedEndDateMillis = dateStringToLong("2023-05-20"),
//        initialDisplayMode =  DisplayMode.Input)
////    MaterialTheme {
//    dateStringToLong("2023-05-20")
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = 0L,
        initialSelectedEndDateMillis = 57878550L,
        initialDisplayMode =  DisplayMode.Input)
    Surface() {
        BSHelperTheme {
            Column {
                Card {
                    BSDateRangePicker(
                        state = dateRangePickerState,
                        title = {}
                    )
                }
            }

        }

    }

//    }
}