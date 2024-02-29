package io.ktlab.bshelper.ui.screens.beatsaver.components.filters

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.ui.screens.beatsaver.components.TitleLabel
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RatingRangeSlider(
    ratingSliderValues: Pair<Double?, Double?>,
    limit: Pair<Double, Double> = 0.0 to 1.0,
    onRatingRangeChange: (Pair<Double?, Double?>) -> Unit,
    step: Int = 20,
) {
    val ratingSliderValue = (ratingSliderValues.first ?: limit.first).toFloat()..(ratingSliderValues.second ?: limit.second).toFloat()
    Column {
        Row {
//            PlainTooltipBox(
//                tooltip = { Text("Rating of the map.") },
//            ){
                TitleLabel(
                    "Rating",
//                    modifier = Modifier.tooltipAnchor()
                    )
//            }
            val text = """${ratingSliderValue.start.takeIf { it > limit.first }?.times(100)?.toInt() ?: 0}% - ${
                ratingSliderValue.endInclusive.takeIf { it < limit.second }?.times(100)?.toInt() ?: 100}%"""
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
            value = ratingSliderValue,
            onValueChange = { range ->
                onRatingRangeChange(
                    range.start.toDouble().takeIf { it > limit.first } to range.endInclusive.toDouble().takeIf { it < limit.second },
                )
            },
            valueRange = limit.first.toFloat()..limit.second.toFloat(),
            onValueChangeFinished = {},
            steps = step,
        )
    }
}
