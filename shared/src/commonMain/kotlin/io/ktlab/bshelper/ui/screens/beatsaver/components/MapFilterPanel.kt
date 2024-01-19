package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import io.ktlab.bshelper.ui.event.BeatSaverUIEvent
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.beatsaver.components.filters.DateRangeSelector
import io.ktlab.bshelper.ui.screens.beatsaver.components.filters.DurationRangeSlider
import io.ktlab.bshelper.ui.screens.beatsaver.components.filters.NPSRangeSlider
import io.ktlab.bshelper.ui.screens.beatsaver.components.filters.RatingRangeSlider
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

fun millisToDateFormatString(millis: Long?): String? {
    return if (millis == null) {
        null
    } else {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        sdf.format(Date(millis))
    }
}

fun extractMapTagByString(
    tag: String?,
    type: MapTagType,
): List<MapTag> {
    if (tag == null) {
        return emptyList()
    }
    val tagItem = tag.split(",")
    val result = mutableListOf<MapTag>()
    for (item in tagItem) {
        val genreMapTag = MapTag.allMapTags.find { it.slug == item && it.type == type }
        if (genreMapTag != null) {
            result.add(genreMapTag)
        }
    }
    return result
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
    var selectedStyleTagStates by remember { mutableStateOf(extractMapTagByString(mapFilterPanelState.tags,MapTagType.Style)) }
    var selectedGenreTagStates by remember { mutableStateOf(extractMapTagByString(mapFilterPanelState.tags, MapTagType.Genre)) }
    var featureSelectedState by remember { mutableStateOf(mapFilterPanelState.mapFeatureTagsMap()) }
    val generateTag = {
        val styleTag = selectedStyleTagStates.joinToString(",") { it.slug }
        val genreTag = selectedGenreTagStates.joinToString(",") { it.slug }
        val tag = if (styleTag.isNotEmpty() && genreTag.isNotEmpty()) {
            "$styleTag,$genreTag"
        } else {
            styleTag + genreTag
        }
        tag
    }
    val updateFilter = { it: MapFilterParam -> onUIEvent(BeatSaverUIEvent.UpdateMapFilterParam(it)) }
    val clearFilter = {
        dateRangePickerState.setSelection(null, null)
        featureSelectedState = mapFilterPanelState.mapFeatureTagsMap()
        updateFilter(MapFilterParam.default)
    }

    Column(
        modifier = Modifier.wrapContentSize(),
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
                            featureSelectedState = if (featureSelectedState[it] == true) { featureSelectedState - it } else { featureSelectedState + (it to true) }
                            updateFilter(mapFilterPanelState.copy(
                                chroma = featureSelectedState[MapFeatureTag.Chroma],
                                cinema = featureSelectedState[MapFeatureTag.Cinema],
                                noodle = featureSelectedState[MapFeatureTag.Noodle],
                                me = featureSelectedState[MapFeatureTag.MappingExtensions],
                                ranked = featureSelectedState[MapFeatureTag.Ranked],
                                curated = featureSelectedState[MapFeatureTag.Curated],
                                verified = featureSelectedState[MapFeatureTag.VerifiedMapper],
                                fullSpread = featureSelectedState[MapFeatureTag.FullSpread],
                                automapper = featureSelectedState[MapFeatureTag.AI],
                            ))
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
                        selected = selectedStyleTagStates.contains(it),
                        onClick = {
                            selectedStyleTagStates = if (selectedStyleTagStates.contains(it)) {
                                selectedStyleTagStates - it
                            } else {
                                selectedStyleTagStates + it
                            }
                            updateFilter(mapFilterPanelState.copy(
                                tags = generateTag(),
                            ))
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
                        selected = selectedGenreTagStates.contains(it),
                        onClick = {
                            selectedGenreTagStates = if (selectedGenreTagStates.contains(it)) {
                                selectedGenreTagStates - it
                            } else {
                                selectedGenreTagStates + it
                            }
                            updateFilter(mapFilterPanelState.copy(
                                tags = generateTag(),
                            ))
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
