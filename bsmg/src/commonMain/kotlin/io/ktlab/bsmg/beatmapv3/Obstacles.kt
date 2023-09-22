package io.ktlab.bsmg.beatmapv3

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @see <a href="https://bsmg.wiki/mapping/difficulty-format-v3.html#color-notes">color notes</a>
 */
@Serializable
data class Obstacles(
    @SerialName("b") val beat: Double,
//    An integer number, from 0 to 3, which represents the column where this obstacle is located.
//    The far left column is located at index 0, and increases to the far right column located at index 3.
    @SerialName("x") val x: Int,
    // An integer number, from 0 to 2, which represents the layer where base of the obstacle is located.
    // The bottommost layer is located at layer 0, and increases to the topmost layer located at index 2.
    // 0 grounded, 1 prone, 2 crouch
    @SerialName("y") val y: Int,
//  The time, in beats, that the obstacle extends for (duration).
//  While d can go into negative numbers, be aware that this has some unintended effects.
    @SerialName("d") val duration: Float,
    // width of the obstacle. negative numbers are allowed but has some unintended effect.
    @SerialName("w") val width: Int,
    // height of the obstacle. negative numbers are allowed but has some unintended effect.
    @SerialName("h") val height: Int,
)