package io.ktlab.bshelper.ui.screens.beatsaver.components.filters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.beatmaps.common.formatTime
import io.ktlab.bshelper.ui.screens.beatsaver.components.TitleLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationRangeSlider(
    durationSliderValues: Pair<Double?, Double?>,
    limit: Pair<Double, Double> = 0.0 to 300.0,
    onDurationRangeChange: (Pair<Double?, Double?>) -> Unit,
    step: Int = 10,
) {
    val durationSliderValue = (durationSliderValues.first ?: limit.first).toFloat()..(durationSliderValues.second ?: limit.second).toFloat()
    Column {
        Row {
//            PlainTooltipBox(
//                tooltip = { Text("Duration of the song.") },
//            ) {
                TitleLabel(
                    "Duration",
//                    modifier = Modifier.tooltipAnchor()
                )
//            }
            val text = """${durationSliderValue.start.takeIf { it > limit.first }?.formatTime() ?: 0.0f.formatTime()} - ${
                durationSliderValue.endInclusive.takeIf { it < limit.second }?.formatTime() ?: "∞"}"""
            Text(
                text = text,
                modifier =
                    Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically),
            )
        }
        RangeSlider(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = durationSliderValue,
            onValueChange = { range ->
                onDurationRangeChange(
                    range.start.toDouble().takeIf { it > limit.first } to range.endInclusive.toDouble().takeIf { it < limit.second },
                )
            },
            valueRange = limit.first.toFloat()..limit.second.toFloat(),
            onValueChangeFinished = {},
            steps = step,
        )
    }
}
