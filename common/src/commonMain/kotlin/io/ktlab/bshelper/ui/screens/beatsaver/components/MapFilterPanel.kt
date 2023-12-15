package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import io.beatmaps.common.formatTime
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.model.dto.request.MapFilterParam
import io.ktlab.bshelper.model.enums.MapFeatureTag
import io.ktlab.bshelper.model.enums.MapTag
import io.ktlab.bshelper.model.enums.MapTagType
import io.ktlab.bshelper.ui.components.BSDateRangePicker
import io.ktlab.bshelper.ui.components.ChipDropDownSelector
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent
import java.text.SimpleDateFormat
import java.util.*

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

    val durationStart = mapFilterPanelState.minDuration?.toFloat() ?:0f
    val durationEnd = mapFilterPanelState.maxDuration?.toFloat() ?: 300f
    var durationSliderValues by remember { mutableStateOf((durationStart..durationEnd))}
    val ratingStart = mapFilterPanelState.minRating ?:0f
    val ratingEnd = mapFilterPanelState.maxRating ?: 1f
    var ratingSliderValues by remember { mutableStateOf((ratingStart..ratingEnd))}
    val ratingToText = fun (value: Float?): String {
        return if (value == null) { "100%" } else {"${value.times(100).toInt()}%" }
    }
    val npsToText = fun (value: Float?): String {
        return if (value == null||value == 16f) { "∞" } else { String.format("%.2f", value) }
    }
    val durationToText = fun (value: Float?): String {
        return if (value == null||value == 330f) { "∞" } else {
            value.formatTime()
        }
    }
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = dateStringToLong(mapFilterPanelState.from),
        initialSelectedEndDateMillis = dateStringToLong(mapFilterPanelState.to),
        initialDisplayMode =  DisplayMode.Picker)
    var queryKey by remember { mutableStateOf(mapFilterPanelState.queryKey) }

    var selectedStyleTagState by remember { mutableStateOf(findMapTagByString(mapFilterPanelState.tags, MapTagType.Style)) }
    var selectedGenreTagState by remember { mutableStateOf(findMapTagByString(mapFilterPanelState.tags, MapTagType.Genre)) }
    var featureSelectedState by remember { mutableStateOf(mapFilterPanelState.mapFeatureTagsMap()) }
    val (selectedSortOrder, onSortOrderOptionSelected) = remember { mutableStateOf(mapFilterPanelState.sortKey.ifEmpty { "Relevance" }) }

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
            minDuration = if (durationSliderValues.start == 0f) { null } else { durationSliderValues.start.toInt() },
            maxDuration = if (durationSliderValues.endInclusive == 300f) { null } else { durationSliderValues.endInclusive.toInt() },
            minRating = if (ratingSliderValues.start == 0f) { null } else { ratingSliderValues.start },
            maxRating = if (ratingSliderValues.endInclusive == 1f) { null } else { ratingSliderValues.endInclusive },
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
    val updateFilter = {
        onUIEvent(BeatSaverUIEvent.UpdateMapFilterParam(toMapFilterPanelState()))
    }
    val submitFilter = {
        onUIEvent(BeatSaverUIEvent.SearchMapWithFilter(toMapFilterPanelState()))
    }

    val clearFilter = {
        queryKey = ""
        npsSliderValues = (0f..16f)
        durationSliderValues = (0f..300f)

        dateRangePickerState.setSelection(null, null)
        onSortOrderOptionSelected("Relevance")
        featureSelectedState = MapFeatureTag.mapFeatureTags.associateWith { false }
        selectedStyleTagState = null
        selectedGenreTagState = null
        updateFilter()
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
                    onValueChange = { queryKey = it;updateFilter() },
                    label = { Text(text = "Search") },
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    shape = MaterialTheme.shapes.large,
                )
            }
            // NPSRangeSelector
            Row {
                PlainTooltipBox(
                    tooltip = {
                        Text(
                            text = "Notes Per Second. take max NPS of all difficulties"
                        )
                    }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .align(Alignment.CenterVertically)
                            .tooltipAnchor(),
                        text ="NPS "
                    )
                }
                val text = npsToText(npsSliderValues.start) + " - " + npsToText(npsSliderValues.endInclusive)
                Text(text = text, modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .align(Alignment.CenterVertically),)
            }
            RangeSlider(
                modifier = Modifier.padding(horizontal = 16.dp),
                value = npsSliderValues,
                onValueChange = { npsSliderValues = it;updateFilter() },
                valueRange = 0f..16f,
                onValueChangeFinished = {},
                steps = 0,
            )
            // DurationRangeSelector
//            Divider(modifier = Modifier.padding(4.dp))
            Row {
                PlainTooltipBox(
                    tooltip = { Text("Duration of the song.") }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .align(Alignment.CenterVertically)
                            .tooltipAnchor(),
                        text ="Duration "
                    )
                }
                val text = durationToText(durationSliderValues.start) + " - " + durationToText(durationSliderValues.endInclusive)
                Text(text = text, modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .align(Alignment.CenterVertically),)
            }
            RangeSlider(
                modifier = Modifier.padding(horizontal = 16.dp),
                value = durationSliderValues,
                onValueChange = { durationSliderValues = it;updateFilter() },
                valueRange = 0f..330f,
                onValueChangeFinished = {},
                steps = 10,
            )
            // RatingRangeSelector
//            Divider(modifier = Modifier.padding(4.dp))
            Row {
                PlainTooltipBox(
                    tooltip = { Text("score = positive/total, rating = score - (score-0.5)/2^{lg(total+1)}") }
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .align(Alignment.CenterVertically)
                            .tooltipAnchor(),
                        text ="Rating "
                    )
                }
                val text = ratingToText(ratingSliderValues.start) + " - " + ratingToText(ratingSliderValues.endInclusive)
                Text(text = text, modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .align(Alignment.CenterVertically),)
            }
            RangeSlider(
                modifier = Modifier.padding(horizontal = 16.dp),
                value = ratingSliderValues,
                onValueChange = { ratingSliderValues = it;updateFilter() },
                valueRange = 0f..1f,
                onValueChangeFinished = {},
                steps = 19,
            )
        //        SortBySelector

            val options = listOf("Relevance","Latest", "Rating", "Curated")
            Row (
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text("Sort By", Modifier.padding(4.dp), style = MaterialTheme.typography.titleMedium)
                ChipDropDownSelector(options,selectedSortOrder, modifier = Modifier.padding(4.dp)) {
                    onSortOrderOptionSelected(it);updateFilter()
                }

            }

            Text("Date Selector", Modifier.padding(4.dp), style = MaterialTheme.typography.labelMedium)
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ){
                var dateRangePickerDialogOpen by remember { mutableStateOf(false) }
                TextButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = { dateRangePickerDialogOpen = true }
                ) {
                    val start = millisToDateFormatString(dateRangePickerState.selectedStartDateMillis) ?: "Start"
                    val end = millisToDateFormatString(dateRangePickerState.selectedEndDateMillis) ?: "End"
                    Text(
                        "$start - $end",
                        modifier=Modifier.padding(2.dp)
                    )
                    Icon(Icons.Filled.Edit, stringResource(MR.strings.edit))
                }
                if (dateRangePickerDialogOpen){
                    BSDateRangePicker(
                        state = dateRangePickerState,
                        title = {},
                        onDismissRequest = { dateRangePickerDialogOpen = false },
                        onConfirm = { dateRangePickerDialogOpen = false;updateFilter() },
                    )
                }
            }

//            FeatureSelector
            DividerWithTitle("Feature Selector")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                MapFeatureTag.mapFeatureTags.forEach {
                    FilterChip(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        selected = featureSelectedState[it] ?: false,
                        onClick = {
                            featureSelectedState = if (featureSelectedState[it] == true) {
                                featureSelectedState - it
                            } else {
                                featureSelectedState + (it to true)
                            }
                            updateFilter()
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
                MapTag.styleMapTags.map {
                    ElevatedFilterChip(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        selected = it == selectedStyleTagState,
                        onClick = {
                            selectedStyleTagState = if (it == selectedStyleTagState) { null } else { it }
                            updateFilter()
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
                MapTag.genreMapTags.map {
                    ElevatedFilterChip(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        selected = it == selectedGenreTagState,
                        onClick = {
                            selectedGenreTagState = if (it == selectedGenreTagState) { null } else { it }
                            updateFilter()
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
fun DividerWithTitle(title: String){
    Column{
//        Divider(modifier = Modifier.padding(horizontal = 4.dp))
        Text(
            modifier = Modifier
                .padding(4.dp),
            text =title,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

