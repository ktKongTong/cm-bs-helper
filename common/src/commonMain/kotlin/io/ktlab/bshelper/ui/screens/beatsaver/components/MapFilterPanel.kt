package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.model.dto.request.MapFilterParam
import io.ktlab.bshelper.model.enums.MapFeatureTag
import io.ktlab.bshelper.model.enums.MapTag
import io.ktlab.bshelper.model.enums.MapTagType
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent
import java.text.SimpleDateFormat
import java.util.Date

fun millisToDateFormatString(millis: Long?): String? {
    return if (millis == null) { null } else {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        sdf.format(Date(millis))
    }
}
fun dateStringToLong(dateString: String?): Long? {
    return if ((dateString == null) or (dateString == "Start Date") or (dateString == "End Date")) { null } else {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        sdf.parse(dateString)!!.time
    }
}

fun findMapTagByString(tag: String?,type: MapTagType): MapTag? {
    if (tag == null) { return null }
    val tagItem = tag.split(",")
    for (item in tagItem) {
        val genreMapTag = MapTag.allMapTags.find { it.slug == item && it.type == type }
        if (genreMapTag != null) { return genreMapTag }
    }
    return null
}


// a bullshit component but useful
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MapFilterPanel(
    mapFilterPanelState: MapFilterParam,
    onUIEvent: (UIEvent) -> Unit = {},
){
    val npsStart = mapFilterPanelState.minNps?.toFloat() ?:0f
    val npsEnd = mapFilterPanelState.maxNps?.toFloat() ?:16f
    var npsSliderValues by remember { mutableStateOf((npsStart..npsEnd))}
    val npsToText = fun (value: Float?): String {
        return if (value == null||value == 16f) { "∞" } else { String.format("%.2f", value) }
    }

    var dateRangePickerDialogOpen by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = dateStringToLong(mapFilterPanelState.from),
        initialSelectedEndDateMillis = dateStringToLong(mapFilterPanelState.to),
        initialDisplayMode =  DisplayMode.Input)
    var queryKey by remember { mutableStateOf(mapFilterPanelState.queryKey) }

    var selectedStyleTagState by remember { mutableStateOf(findMapTagByString(mapFilterPanelState.tags, MapTagType.Style)) }
    var selectedGenreTagState by remember { mutableStateOf(findMapTagByString(mapFilterPanelState.tags, MapTagType.Genre)) }
    var featureSelectedState by remember { mutableStateOf(mapOf(
        MapFeatureTag.AI to mapFilterPanelState.automapper,
        MapFeatureTag.Chroma to mapFilterPanelState.chroma,
        MapFeatureTag.Noodle to mapFilterPanelState.noodle,
        MapFeatureTag.MappingExtensions to mapFilterPanelState.me,
        MapFeatureTag.Cinema to mapFilterPanelState.cinema,
        MapFeatureTag.Ranked to mapFilterPanelState.ranked,
        MapFeatureTag.Curated to mapFilterPanelState.curated,
        MapFeatureTag.VerifiedMapper to mapFilterPanelState.verified,
        MapFeatureTag.FullSpread to mapFilterPanelState.fullSpread,
    )) }
    val (selectedSortOrder, onSortOrderOptionSelected) = remember { mutableStateOf(mapFilterPanelState.sortKey.ifEmpty { "Relevance" }) }
    var searchBarActive by remember { mutableStateOf(false) }

    val convertToTagString = fun (): String? {
        return when {
            selectedStyleTagState == null && selectedGenreTagState == null -> { null }
            selectedStyleTagState != null && selectedGenreTagState != null -> {
                selectedStyleTagState!!.slug + "," + selectedGenreTagState!!.slug
            }
            selectedStyleTagState != null -> { selectedStyleTagState!!.slug }
            else -> { selectedGenreTagState!!.slug }
        }
    }

    val toMapFilterPanelState = fun (): MapFilterParam {
        return mapFilterPanelState.copy(
            queryKey = queryKey,
            sortKey = selectedSortOrder,
            minNps = if (npsSliderValues.start == 0f) { null } else { npsSliderValues.start.toDouble() },
            maxNps = if (npsSliderValues.endInclusive == 16f) { null } else { npsSliderValues.endInclusive.toDouble() },
            tags = convertToTagString(),
            from = millisToDateFormatString(dateRangePickerState.selectedStartDateMillis),
            to = millisToDateFormatString(dateRangePickerState.selectedEndDateMillis),
            automapper = featureSelectedState[MapFeatureTag.AI],
            chroma = featureSelectedState[MapFeatureTag.Chroma],
            noodle = featureSelectedState[MapFeatureTag.Noodle],
            me = featureSelectedState[MapFeatureTag.MappingExtensions],
            cinema = featureSelectedState[MapFeatureTag.Cinema],
            ranked = featureSelectedState[MapFeatureTag.Ranked],
            curated = featureSelectedState[MapFeatureTag.Curated],
            verified = featureSelectedState[MapFeatureTag.VerifiedMapper],
            fullSpread = featureSelectedState[MapFeatureTag.FullSpread],
        )
    }


    val clearFilter = {
        queryKey = ""
        npsSliderValues = (0f..16f)
        dateRangePickerState.setSelection(null, null)
        onSortOrderOptionSelected("Relevance")
        featureSelectedState = MapFeatureTag.mapFeatureTags.associateWith { false }
        selectedStyleTagState = null
        selectedGenreTagState = null
    }
    val submitFilter = {
        onUIEvent(BeatSaverUIEvent.SearchMapWithFilter(toMapFilterPanelState()))
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
//        SearchBar
//            SearchBar(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(4.dp),
//                query = queryKey,
//                onQueryChange = { queryKey = it },
//                onSearch = { searchBarActive = false },
//                active = searchBarActive,
//                placeholder = { Text("Search") },
//                onActiveChange = { searchBarActive = it }
//            ) {
//                Icon(Icons.Filled.Search, "search")
//            }

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
            RadioButtons(selectedOption= selectedSortOrder,onOptionSelected = onSortOrderOptionSelected)

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
                        modifier=Modifier.padding(2.dp)
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
                MapFeatureTag.mapFeatureTags.forEach {
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

            // StyleTagSelector
            DividerWithTitle("Style Selector")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                MapTag.styleMapTags.forEach {
                    ElevatedFilterChip(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        selected = it == selectedStyleTagState,
                        onClick = {
                            selectedStyleTagState = if (it == selectedStyleTagState) { null } else { it }
                        },
                        leadingIcon = if (selectedStyleTagState == it) { {
                            Icon(Icons.Filled.Check, stringResource(MR.strings.clear))
                        } } else { null },
                        label = { Text(it.human) },
                    )
                }
            }

            DividerWithTitle("Genre Selector")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                MapTag.genreMapTags.forEach {
                    ElevatedFilterChip(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        selected = it == selectedGenreTagState,
                        onClick = {
                            selectedGenreTagState = if (it == selectedGenreTagState) { null } else { it }
                        },
                        leadingIcon = if (selectedGenreTagState == it) { {
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
            TextButton(onClick = submitFilter) {
                Icon(Icons.Filled.Web, stringResource(MR.strings.confirm))
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = stringResource(MR.strings.confirm)
                )
            }
        }
    }
}


@Composable
fun RadioButtons(
    selectedOption: String,
    onOptionSelected: (String) -> Unit = {},
) {
    val radioOptions = listOf("Relevance","Latest", "Rating", "Curated")
    RadioButtons(radioOptions,selectedOption,onOptionSelected)
}


@Composable
fun DividerWithTitle(title: String){
    Column{
        Divider(modifier = Modifier.padding(horizontal = 4.dp))
        Text(
            modifier = Modifier
                .padding(4.dp),
            text =title,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
