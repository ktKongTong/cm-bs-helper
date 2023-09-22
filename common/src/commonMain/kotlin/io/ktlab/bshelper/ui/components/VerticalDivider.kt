package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VerticalDivider(){
    Divider(
        thickness = 2.dp,
        modifier = Modifier.fillMaxHeight()
            .padding(vertical = 16.dp)
            .width(1.dp)
    )
}