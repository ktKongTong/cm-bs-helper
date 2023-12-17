package io.ktlab.bshelper.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.Placeable

@Composable
fun ResizeTwoColumnHeightRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val measurePolicy = resizeTwoColumnHeightRowMeasurePolicy()
    Layout(
        measurePolicy = measurePolicy,
        content = content,
        modifier = modifier,
    )
}

fun resizeTwoColumnHeightRowMeasurePolicy() =
    MeasurePolicy { measurables, constraints ->
        val first = measurables.first().measure(constraints)
        val height = first.height
        val width = constraints.maxWidth
        val resizedPlaceables: List<Placeable> =
            measurables.drop(1).map {
                it.measure(constraints.copy(maxHeight = height, maxWidth = constraints.maxWidth - first.width))
            }
        layout(width, height) {
            val components = (listOf(first) + resizedPlaceables)
            components.forEachIndexed { index, placeable ->
                val widthStart = components.take(index).sumOf { it.measuredWidth }
                placeable.place(widthStart, 0)
            }
        }
    }
