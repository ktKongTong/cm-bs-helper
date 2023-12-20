package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.model.dto.request.MapFilterParam
import io.ktlab.bshelper.model.enums.MapFeatureTag
import io.ktlab.bshelper.model.enums.MapTag
import io.ktlab.bshelper.model.enums.MapTagType
import io.ktlab.bshelper.ui.components.BSSearchBar
import io.ktlab.bshelper.ui.components.ChipDropDownSelector
import io.ktlab.bshelper.ui.components.buttons.ClearTextButton
import io.ktlab.bshelper.ui.components.buttons.QueryTextButton
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.beatsaver.components.filters.DateRangeSelector
import io.ktlab.bshelper.ui.screens.beatsaver.components.filters.DurationRangeSlider
import io.ktlab.bshelper.ui.screens.beatsaver.components.filters.NPSRangeSlider
import io.ktlab.bshelper.ui.screens.beatsaver.components.filters.RatingRangeSlider
import io.ktlab.bshelper.ui.event.BeatSaverUIEvent
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.*

fun millisToDateFormatString(millis: Long?): String? {
    return if (millis == null) {
        null
    } else {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        sdf.format(Date(millis))
    }
}

fun dateStringToLong(dateString: String?): Long? {
    return if ((dateString == null) or (dateString == "Start Date") or (dateString == "End Date")) {
        null
    } else {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        sdf.parse(dateString)!!.time
    }
}

fun findMapTagByString(
    tag: String?,
    type: MapTagType,
): MapTag? {
    if (tag == null) {
        return null
    }
    val tagItem = tag.split(",")
    for (item in tagItem) {
        val genreMapTag = MapTag.allMapTags.find { it.slug == item && it.type == type }
        if (genreMapTag != null) {
            return genreMapTag
        }
    }
    return null
}

// a bullshit component but useful, maybe improve it later
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MapFilterPanel(
    mapFilterPanelState: MapFilterParam,
    onUIEvent: (UIEvent) -> Unit = {},
) {
    val dateRangePickerState =
        rememberDateRangePickerState(
            initialSelectedStartDateMillis = mapFilterPanelState.to?.atStartOfDayIn(TimeZone.currentSystemDefault())?.toEpochMilliseconds(),
            initialSelectedEndDateMillis = mapFilterPanelState.to?.atStartOfDayIn(TimeZone.currentSystemDefault())?.toEpochMilliseconds(),
            initialDisplayMode = DisplayMode.Picker,
        )

    var selectedStyleTagState by remember { mutableStateOf(findMapTagByString(mapFilterPanelState.tags, MapTagType.Style)) }
    var selectedGenreTagState by remember { mutableStateOf(findMapTagByString(mapFilterPanelState.tags, MapTagType.Genre)) }
    var featureSelectedState by remember { mutableStateOf(mapFilterPanelState.mapFeatureTagsMap()) }

    val updateFilter = { it: MapFilterParam ->
        onUIEvent(BeatSaverUIEvent.UpdateMapFilterParam(it))
    }
    val clearFilter = {
        dateRangePickerState.setSelection(null, null)
        selectedStyleTagState = null
        selectedGenreTagState = null
        featureSelectedState = mapFilterPanelState.mapFeatureTagsMap()
        updateFilter(MapFilterParam.default)
    }

    Column(
        modifier =
            Modifier
                .wrapContentSize(),
    ) {
        Column(
            modifier =
                Modifier
                    .wrapContentHeight()
                    .verticalScroll(rememberScrollState())
                    .weight(weight = 1f, fill = false),
        ) {
            BSSearchBar(
                query = mapFilterPanelState.queryKey,
                onQueryChange = { updateFilter(mapFilterPanelState.copy(queryKey = it)) },
                onClear = { updateFilter(mapFilterPanelState.copy(queryKey = "")) },
            )
            NPSRangeSlider(
                npsSliderValues = mapFilterPanelState.minNps to mapFilterPanelState.maxNps,
                limit = 0.0 to 16.0,
                onNPSRangeChange = { updateFilter(mapFilterPanelState.copy(minNps = it.first, maxNps = it.second)) },
            )
            DurationRangeSlider(
                durationSliderValues = mapFilterPanelState.minDuration?.toDouble() to mapFilterPanelState.maxDuration?.toDouble(),
                limit = 0.0 to 330.0,
                onDurationRangeChange = {
                    updateFilter(
                        mapFilterPanelState.copy(minDuration = it.first?.toInt(), maxDuration = it.second?.toInt()),
                    )
                },
                step = 10,
            )
            RatingRangeSlider(
                ratingSliderValues = mapFilterPanelState.minRating?.toDouble() to mapFilterPanelState.maxRating?.toDouble(),
                limit = 0.0 to 1.0,
                onRatingRangeChange = {
                    updateFilter(mapFilterPanelState.copy(minRating = it.first?.toFloat(), maxRating = it.second?.toFloat()))
                },
                step = 19,
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TitleLabel("Sort By")
                ChipDropDownSelector(
                    options = remember { listOf("Relevance", "Latest", "Rating", "Curated") },
                    selectedOption = mapFilterPanelState.sortKey,
                    modifier = Modifier.padding(4.dp),
                ) { updateFilter(mapFilterPanelState.copy(sortKey = it)) }
            }
            DateRangeSelector(dateRangePickerState) {
                updateFilter(
                    mapFilterPanelState.copy(
                        from =
                            dateRangePickerState.selectedStartDateMillis?.let {
                                Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
                            },
                        to =
                            dateRangePickerState.selectedEndDateMillis?.let {
                                Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
                            },
                    ),
                )
            }

//            FeatureSelector
            TitleLabel("Feature Selector")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                MapFeatureTag.mapFeatureTags.forEach {
                    FilterChip(
                        elevation = null,
                        modifier = Modifier.padding(horizontal = 4.dp),
                        selected = featureSelectedState[it] ?: false,
                        onClick = {
                            featureSelectedState =
                                if (featureSelectedState[it] == true) {
                                    featureSelectedState - it
                                } else {
                                    featureSelectedState + (it to true)
                                }
                            updateFilter(mapFilterPanelState)
                        },
                        leadingIcon =
                            if (featureSelectedState[it] == true) {
                                {
                                    Icon(Icons.Filled.Check, stringResource(MR.strings.clear))
                                }
                            } else {
                                null
                            },
                        label = { Text(it.human) },
                    )
                }
            }

            // StyleTagSelector
            TitleLabel("Style Selector")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                MapTag.styleMapTags.map {
                    FilterChip(
                        elevation = null,
                        modifier = Modifier.padding(horizontal = 4.dp),
                        selected = it == selectedStyleTagState,
                        onClick = {
                            selectedStyleTagState =
                                if (it == selectedStyleTagState) {
                                    null
                                } else {
                                    it
                                }
                            updateFilter(mapFilterPanelState)
                        },
                        leadingIcon =
                            if (selectedStyleTagState == it) {
                                {
                                    Icon(Icons.Filled.Check, stringResource(MR.strings.clear))
                                }
                            } else {
                                null
                            },
                        label = { Text(it.human) },
                    )
                }
            }

            TitleLabel("Genre Selector")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                MapTag.genreMapTags.map {
                    FilterChip(
                        elevation = null,
                        modifier = Modifier.padding(horizontal = 4.dp),
                        selected = it == selectedGenreTagState,
                        onClick = {
                            selectedGenreTagState =
                                if (it == selectedGenreTagState) {
                                    null
                                } else {
                                    it
                                }
                            updateFilter(mapFilterPanelState)
                        },
                        leadingIcon =
                            if (selectedGenreTagState == it) {
                                {
                                    Icon(Icons.Filled.Check, stringResource(MR.strings.clear))
                                }
                            } else {
                                null
                            },
                        label = { Text(it.human) },
                    )
                }
            }
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            ClearTextButton { clearFilter() }
            QueryTextButton { onUIEvent(BeatSaverUIEvent.SearchMapWithFilter(mapFilterPanelState)) }
        }
    }
}

@Composable
fun DividerWithTitle(title: String) {
    Column {
//        Divider(modifier = Modifier.padding(horizontal = 4.dp))
        Text(
            modifier =
                Modifier
                    .padding(4.dp),
            text = title,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
fun TitleLabel(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier =
            modifier
                .padding(4.dp),
        text = title,
        style = MaterialTheme.typography.titleMedium,
    )
}
