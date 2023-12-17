package io.ktlab.bsmg.beatmapv3

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * chain and links.
 * @see <a href="https://bsmg.wiki/mapping/difficulty-format-v3#burst-sliders">burst sliders</a>
 */
@Serializable
data class BurstSliders(
    @SerialName("b") val headBeatTime: Double,
    @SerialName("c") val color: Int,
    @SerialName("x") val headX: Int,
    @SerialName("y") val headY: Int,
    // 8 function as down
    @SerialName("d") val direction: Int,
    @SerialName("tb") val tailBeatTime: Double,
    @SerialName("tx") val tailX: Int,
    @SerialName("ty") val tailY: Int,
    @SerialName("sc") val segmentCount: Int,
    // This is the proportion of how much of the path from to is used by the chain.
    @SerialName("s") val squishFactor: Double,
)
