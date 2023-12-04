package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.model.dto.request.PlaylistFilterParam
import io.ktlab.bshelper.model.enums.MapFeatureTag
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlaylistFilterPanel(
    playlistFilterPanelState: PlaylistFilterParam,
    onUIEvent: (UIEvent) -> Unit = {},
){
    val npsStart = playlistFilterPanelState.minNps?.toFloat() ?:0f
    val npsEnd = playlistFilterPanelState.maxNps?.toFloat() ?:16f
    var npsSliderValues by remember { mutableStateOf((npsStart..npsEnd)) }
    val npsToText = fun (value: Float?): String {
        return if (value == null||value == 16f) { "∞" } else { String.format("%.2f", value) }
    }

    var dateRangePickerDialogOpen by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = dateStringToLong(playlistFilterPanelState.from),
        initialSelectedEndDateMillis = dateStringToLong(playlistFilterPanelState.to),
        initialDisplayMode =  DisplayMode.Input)
    var queryKey by remember { mutableStateOf(playlistFilterPanelState.queryKey) }

//    var featureSelectedState  by remember { mutableStateOf(set) }
    var featureSelectedState by remember { mutableStateOf(mapOf(
        MapFeatureTag.Curated to playlistFilterPanelState.curated,
        MapFeatureTag.VerifiedMapper to playlistFilterPanelState.verified,
    )) }
    val (selectedSortOrder, onSortOrderOptionSelected) = remember { mutableStateOf(playlistFilterPanelState.sortKey.ifEmpty { "Relevance" }) }

    val toPlaylistFilterPanelState = fun (): PlaylistFilterParam {
        return playlistFilterPanelState.copy(
            queryKey = queryKey,
            sortKey = selectedSortOrder,
            minNps = if (npsSliderValues.start == 0f) { null } else { npsSliderValues.start.toDouble() },
            maxNps = if (npsSliderValues.endInclusive == 16f) { null } else { npsSliderValues.endInclusive.toDouble() },
            from = millisToDateFormatString(dateRangePickerState.selectedStartDateMillis),
            to = millisToDateFormatString(dateRangePickerState.selectedEndDateMillis),
            curated = featureSelectedState[MapFeatureTag.Curated],
            verified = featureSelectedState[MapFeatureTag.VerifiedMapper],
        )
    }


    val clearFilter = {
        queryKey = ""
        npsSliderValues = (0f..16f)
        dateRangePickerState.setSelection(null, null)
        onSortOrderOptionSelected("Relevance")
        featureSelectedState = MapFeatureTag.mapFeatureTags.associateWith { false }
    }
    Column (
        modifier = Modifier
            .wrapContentSize()
    ){
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .verticalScroll(rememberScrollState())
                .weight(weight = 1f, fill = false)
        ){

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val focusManager = LocalFocusManager.current
                OutlinedTextField(
                    value = queryKey,
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "search icon") },
                    onValueChange = { queryKey = it },
                    label = { Text(text = "Search") },
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    shape = RoundedCornerShape(10.dp),
                )
            }
            // NPSRangeSelector
            Divider(modifier = Modifier.padding(4.dp))
            Row {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically),
                    text ="NPS "
                )
                val text = npsToText(npsSliderValues.start) + " - " + npsToText(npsSliderValues.endInclusive)
                Text(text = text, modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .align(Alignment.CenterVertically),)
            }
            RangeSlider(
                modifier = Modifier.padding(horizontal = 16.dp),
                value = npsSliderValues,
                onValueChange = { npsSliderValues = it },
                valueRange = 0f..16f,
                onValueChangeFinished = {},
                steps = 0,
            )
//        SortBySelector
            DividerWithTitle("Sort By")
            FilterChipGroup(options = listOf("Relevance","Latest","Curated"),selectedOption= selectedSortOrder,onOptionSelected = onSortOrderOptionSelected)

//        DateRangePicker
//            TODO updateState
            DividerWithTitle("Date Selector")
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ){
                TextButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = { dateRangePickerDialogOpen = true }
                ) {
                    val start = millisToDateFormatString(dateRangePickerState.selectedStartDateMillis) ?: "Start Date"
                    val end = millisToDateFormatString(dateRangePickerState.selectedEndDateMillis) ?: "End Date"
                    Text(
                        "$start - $end",
                        modifier= Modifier.padding(2.dp)
                    )
                    Icon(Icons.Filled.Edit, stringResource(MR.strings.edit))
                }
            }
            if (dateRangePickerDialogOpen){
                AlertDialog(
                    onDismissRequest = { dateRangePickerDialogOpen = false },
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10)
                    ){
                        DateRangePicker(
                            state = dateRangePickerState,
                            title = {},
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = { dateRangePickerState.setSelection(null, null) }) {
                                Icon(Icons.Filled.Clear, stringResource(MR.strings.clear))
                                Text(
                                    modifier = Modifier.padding(start = 2.dp),
                                    text = stringResource(MR.strings.clear)
                                )
                            }
                            TextButton(onClick = { dateRangePickerDialogOpen = false }) {
                                Icon(Icons.Filled.Check, stringResource(MR.strings.confirm))
                                Text(text = stringResource(MR.strings.confirm))
                            }
                        }
                    }
                }
            }

//            FeatureSelector
            DividerWithTitle("Feature Selector")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                listOf(MapFeatureTag.VerifiedMapper, MapFeatureTag.Curated).forEach {
                    ElevatedFilterChip(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        selected = featureSelectedState[it] ?: false,
                        onClick = {
                            featureSelectedState = if (featureSelectedState[it] == true) {
                                featureSelectedState - it
                            } else {
                                featureSelectedState + (it to true)
                            }
                        },
                        leadingIcon = if (featureSelectedState[it] == true) { {
                            Icon(Icons.Filled.Check, stringResource(MR.strings.clear))
                        } } else { null },
                        label = { Text(it.human) },
                    )
                }
            }
        }

        Row (
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ){
//        ClearButton
            TextButton(onClick = clearFilter) {
                Icon(Icons.Filled.Clear, stringResource(MR.strings.clear))
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = stringResource(MR.strings.clear)
                )
            }
//        ConfirmButton
            TextButton(onClick = { onUIEvent(BeatSaverUIEvent.SearchPlaylistWithFilter(toPlaylistFilterPanelState())) }) {
                Icon(Icons.Filled.Web, stringResource(MR.strings.confirm))
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = stringResource(MR.strings.confirm)
                )
            }
        }
    }
}
