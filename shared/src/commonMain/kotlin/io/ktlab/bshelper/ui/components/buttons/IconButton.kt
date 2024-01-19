package io.ktlab.bshelper.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.material3.OutlinedIconButton as M3IconButton
@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    // round icon button with hover effect
    val interactionSource = remember { MutableInteractionSource() }
    val hovered = interactionSource.collectIsHoveredAsState()
    M3IconButton(
        onClick = onClick,
        modifier = modifier
            .hoverable(interactionSource),
        colors = IconButtonDefaults.outlinedIconButtonColors(
            containerColor = if (hovered.value) Color.Red.copy(alpha = 0.5f) else Color.Transparent,
            contentColor = Color.White,
        ),
        ) {
        content()
    }
}