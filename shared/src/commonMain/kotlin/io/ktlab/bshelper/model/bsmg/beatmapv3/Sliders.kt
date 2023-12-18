package io.ktlab.bshelper.model.bsmg.beatmapv3

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @see <a href="https://bsmg.wiki/mapping/difficulty-format-v3.html#sliders-1">sliders</a>
 */
@Serializable
data class Sliders(
    @SerialName("b") val headBeatTime: Double,
    @SerialName("c") val color: Int,
    @SerialName("x") val headX: Int,
    @SerialName("y") val headY: Int,
    @SerialName("d") val direction: Int,
//     A float which represents how far the arc goes from the head of the arc.
//     If head direction is a dot, this does nothing.
    @SerialName("mu") val multiplier: Double,
    @SerialName("tb") val tailBeatTime: Double,
    @SerialName("tx") val tailX: Int,
    @SerialName("ty") val tailY: Int,
    @SerialName("tc") val tailDirection: Int,
    @SerialName("tmu") val tailMultiplier: Double,
    @SerialName("m") val midAnchorMode: Int,
)
