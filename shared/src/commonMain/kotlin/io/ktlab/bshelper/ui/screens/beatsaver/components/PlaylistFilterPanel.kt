package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.dto.request.PlaylistFilterParam
import io.ktlab.bshelper.model.enums.MapFeatureTag
import io.ktlab.bshelper.ui.components.BSSearchBar
import io.ktlab.bshelper.ui.components.ChipDropDownSelector
import io.ktlab.bshelper.ui.components.buttons.ClearTextButton
import io.ktlab.bshelper.ui.components.buttons.QueryTextButton
import io.ktlab.bshelper.ui.event.UIEvent
import io.ktlab.bshelper.ui.screens.beatsaver.components.filters.DateRangeSelector
import io.ktlab.bshelper.ui.screens.beatsaver.components.filters.NPSRangeSlider
import io.ktlab.bshelper.ui.viewmodel.BeatSaverUIEvent
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlaylistFilterPanel(
    state: PlaylistFilterParam,
    onUIEvent: (UIEvent) -> Unit = {},
) {
    val dateRangePickerState =
        rememberDateRangePickerState(
            initialSelectedStartDateMillis = state.from?.atStartOfDayIn(TimeZone.currentSystemDefault())?.toEpochMilliseconds(),
            initialSelectedEndDateMillis = state.to?.atStartOfDayIn(TimeZone.currentSystemDefault())?.toEpochMilliseconds(),
            initialDisplayMode = DisplayMode.Picker,
        )
    var featureSelectedState by remember { mutableStateOf(state.mapFeatureTagsMap()) }
    val updateFilter = fun (newState: PlaylistFilterParam) {
        onUIEvent(BeatSaverUIEvent.UpdatePlaylistFilterParam(newState))
    }
    val clearFilter = {
        featureSelectedState = state.mapFeatureTagsMap()
        dateRangePickerState.setSelection(null, null)
        updateFilter(PlaylistFilterParam.default)
    }
    Column(Modifier.fillMaxHeight()) {
        Column(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .weight(weight = 1f, fill = false),
        ) {
            BSSearchBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                query = state.queryKey,
                onQueryChange = { updateFilter(state.copy(queryKey = it)) },
                onClear = { updateFilter(state.copy(queryKey = "")) },
            )
            // NPSRangeSelector
            NPSRangeSlider(
                npsSliderValues = state.minNps to state.maxNps,
                limit = 0.0 to 16.0,
                onNPSRangeChange = { updateFilter(state.copy(minNps = it.first, maxNps = it.second)) },
            )
//        SortBySelector
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TitleLabel("Sort By")
                ChipDropDownSelector(
                    options = remember { listOf("Relevance", "Latest", "Curated") },
                    selectedOption = state.sortKey,
                    onSelectedOptionChange = { updateFilter(state.copy(sortKey = it)) },
                )
            }
            DateRangeSelector(dateRangePickerState) {
                updateFilter(
                    state.copy(
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

            TitleLabel("Feature Selector")
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                listOf(MapFeatureTag.VerifiedMapper, MapFeatureTag.Curated).map { tag ->
                    FilterChip(
                        elevation = null,
                        modifier = Modifier.padding(horizontal = 4.dp),
                        selected = featureSelectedState[tag] ?: false,
                        onClick = {
                            featureSelectedState =
                                if (featureSelectedState[tag] == true) {
                                    featureSelectedState - tag
                                } else {
                                    featureSelectedState + (tag to true)
                                }
                        },
                        leadingIcon =
                            if (featureSelectedState[tag] == true) {
                                {
                                    Icon(Icons.Rounded.Check, "Checked Icon")
                                }
                            } else {
                                null
                            },
                        label = { Text(tag.human) },
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            ClearTextButton { clearFilter() }
            QueryTextButton { onUIEvent(BeatSaverUIEvent.SearchPlaylistWithFilter(state)) }
        }
    }
}
