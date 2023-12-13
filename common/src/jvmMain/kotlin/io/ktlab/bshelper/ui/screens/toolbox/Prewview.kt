package io.ktlab.bshelper.ui.screens.toolbox

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.ui.screens.beatsaver.components.BSMapCardPreview
import io.ktlab.bshelper.ui.screens.home.bsmap.PreviewMapCard

@Composable
@Preview
fun Preview() {
    Column {
        PreviewMapCard()
        Row(
            modifier = Modifier.border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(10.dp)

            ).clickable(onClick = { }
            )
        ) {
            BSMapCardPreview()
        }

    }


}