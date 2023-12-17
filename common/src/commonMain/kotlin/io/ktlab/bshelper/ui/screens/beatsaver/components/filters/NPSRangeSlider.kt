package io.ktlab.bshelper.ui.screens.beatsaver.components.filters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.beatmaps.common.fixedStr
import io.ktlab.bshelper.ui.screens.beatsaver.components.TitleLabel

@Composable
fun NPSRangeSlider(
    npsSliderValues: Pair<Double?, Double?>,
    limit: Pair<Double, Double> = 0.0 to 16.0,
    onNPSRangeChange: (Pair<Double?, Double?>) -> Unit,
) {
    val npsSliderValue =(npsSliderValues.first?:limit.first).toFloat()..(npsSliderValues.second?:limit.second).toFloat()
    Column {
        Row {
            TitleLabel("NPS")
            val text = """${npsSliderValue.start.takeIf { it>limit.first }?.fixedStr(2)?:"0"} - ${
                npsSliderValue.endInclusive.takeIf { it < limit.second }?.fixedStr(2) ?: "∞"}"""
            Text(text = text, modifier = Modifier
                .padding(horizontal = 4.dp)
                .align(Alignment.CenterVertically))
        }
        RangeSlider(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = npsSliderValue,
            onValueChange = {range-> onNPSRangeChange(
              range.start.toDouble().takeIf { it > limit.first } to range.endInclusive.toDouble().takeIf { it < limit.second }
            ) },
            valueRange = limit.first.toFloat()..limit.second.toFloat(),
            onValueChangeFinished = {}
        )
    }
}




