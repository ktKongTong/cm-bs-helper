package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.ui.screens.beatsaver.components.millisToDateFormatString
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSelect(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    label: String,
) {
    ElevatedFilterChip(
        modifier = modifier.padding(horizontal = 4.dp),
        colors =
            FilterChipDefaults.elevatedFilterChipColors(
//            containerColor = MaterialTheme.colorScheme,
            ),
        selected = false,
        onClick = onClick,
        label = { Text(label) },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BSDateRangePicker(
    modifier: Modifier = Modifier,
    state: DateRangePickerState,
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit = {},
    quickSelect: List<Pair<String, Long>> =
        listOf(
            "1 week" to Clock.System.now().minus(7L.days).toEpochMilliseconds(),
            "1 month" to Clock.System.now().minus(30L.days).toEpochMilliseconds(),
            "3 month" to Clock.System.now().minus(90L.days).toEpochMilliseconds(),
            "6 month" to Clock.System.now().minus(180L.days).toEpochMilliseconds(),
            "1 year" to Clock.System.now().minus(365L.days).toEpochMilliseconds(),
        ),
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
    ) {
        Card {
            DateRangePicker(
                modifier =
                    Modifier
                        .weight(1f, fill = false),
                state = state,
                title = {},
//            dateFormatter = DatePickerFormatter("yyyy-MM-dd"),
                headline = {
                    Column {
                        Text(
                            text = "${state.selectedStartDateMillis?.let {
                                millisToDateFormatString(it)
                            } ?: "Start Date"} to ${
                                millisToDateFormatString(state.selectedEndDateMillis)
                                    ?: "End Date"}",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                        FlowRow(
                            modifier =
                                Modifier
                                    .horizontalScroll(rememberScrollState()),
                        ) {
                            quickSelect.map {
                                QuickSelect(
                                    label = it.first,
                                    onClick = {
                                        state.setSelection(it.second, null)
                                    },
                                )
                            }
                        }
                    }
                },
                showModeToggle = false,
            )
            Row(
                Modifier
                    .weight(1f, false)
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = { state.setSelection(null, null) }) {
                    Icon(Icons.Filled.Clear, stringResource(MR.strings.clear))
                    Text(
                        modifier = Modifier.padding(start = 2.dp),
                        text = stringResource(MR.strings.clear),
                    )
                }
                TextButton(onClick = {
                    onDismissRequest()
                    onConfirm()
                }) {
                    Icon(Icons.Filled.Check, stringResource(MR.strings.confirm))
                    Text(text = stringResource(MR.strings.confirm))
                }
            }
        }
    }
}
