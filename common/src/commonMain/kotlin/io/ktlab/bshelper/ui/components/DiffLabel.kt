package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.enums.ECharacteristic
import io.ktlab.bshelper.model.enums.EMapDifficulty

@Composable
fun BSMapDiffLabel(
    difficulty: EMapDifficulty,
    characteristic: ECharacteristic = ECharacteristic.Standard,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    val color =
        remember {
            when (difficulty) {
                EMapDifficulty.Easy -> Color(0xFF009f73)
                EMapDifficulty.Normal -> Color(0xFF1268A1)
                EMapDifficulty.Hard -> Color(0xFFFFA500)
                EMapDifficulty.Expert -> Color(0xFFBB86FC)
                EMapDifficulty.ExpertPlus -> Color(0xFFB52A1C)
            }
        }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 8.dp),
    ) {
        Text(
            text = characteristic.human,
            color = tint,
            modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            softWrap = false,
        )
        Text(
            text = difficulty.human,
            color = color,
            modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            softWrap = false,
        )
    }
}
