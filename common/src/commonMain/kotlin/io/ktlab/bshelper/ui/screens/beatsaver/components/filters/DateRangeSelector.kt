package io.ktlab.bshelper.ui.screens.beatsaver.components.filters

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.ui.components.BSDateRangePicker
import io.ktlab.bshelper.ui.screens.beatsaver.components.TitleLabel
import io.ktlab.bshelper.ui.screens.beatsaver.components.millisToDateFormatString
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelector(
    dateRangeState: DateRangePickerState,
    onChange: () -> Unit,
) {
    Row (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ){
        var expanded by remember { mutableStateOf(false) }
        var dateRangePickerDialogOpen by remember { mutableStateOf(false) }
        val quickSelect = remember{listOf(
            "1 week" to Clock.System.now().minus(7L.days).toEpochMilliseconds(),
            "1 month" to Clock.System.now().minus(30L.days).toEpochMilliseconds(),
            "3 month" to Clock.System.now().minus(90L.days).toEpochMilliseconds(),
            "6 month" to Clock.System.now().minus(180L.days).toEpochMilliseconds(),
            "1 year" to Clock.System.now().minus(365L.days).toEpochMilliseconds(),
        )}
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleLabel("Date")
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier,
            ) {
                Row(
                    Modifier
                        .wrapContentSize()
                        .menuAnchor(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        onClick = { dateRangePickerDialogOpen = true;expanded = false }
                    ) {
                        val start = millisToDateFormatString(dateRangeState.selectedStartDateMillis) ?: "Start"
                        val end = millisToDateFormatString(dateRangeState.selectedEndDateMillis) ?: "End"
                        Text(
                            "$start to $end",
                            modifier= Modifier.padding(2.dp)
                        )
                    }
                    IconButton(onClick = { expanded = true }, modifier = Modifier.align(Alignment.CenterVertically)) {
                        Icon(
                            Icons.Rounded.DateRange,
                            stringResource(MR.strings.edit),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    quickSelect.map {
                        DropdownMenuItem(
                            text = { Text(it.first) },
                            onClick = {
                                dateRangeState.setSelection(it.second, null)
                                expanded = false
                                onChange()
                            }
                        )
                    }
                }
            }
        }
        if (dateRangePickerDialogOpen){
            BSDateRangePicker(
                state = dateRangeState,
                title = {},
                onDismissRequest = { dateRangePickerDialogOpen = false },
                onConfirm = { dateRangePickerDialogOpen = false;onChange() },
            )
        }
    }
}
