package io.ktlab.bshelper.ui.components.shapes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class TriangleShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
//        val path = Path().apply {
//            moveTo(size.width / 2f, 0f)
//            lineTo(size.width, size.height)
//            lineTo(0f, size.height)
//            close()
//        }
//        return Outline.Generic(path)
        TODO()
    }
}
