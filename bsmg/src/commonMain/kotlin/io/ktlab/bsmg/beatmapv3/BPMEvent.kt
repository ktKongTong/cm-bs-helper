package io.ktlab.bsmg.beatmapv3

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @see <a href="https://bsmg.wiki/mapping/difficulty-format-v3.html#bpm-events">bpm events</a>
 */
@Serializable
data class BPMEvent(
//    The time, in beats, where this object reaches the player.
    @SerialName("b") val time: Double,
//    A float representing the new bpm. Any b in objects after this point will be adjusted to the new bpm.
    @SerialName("m") val m: Double,
)
