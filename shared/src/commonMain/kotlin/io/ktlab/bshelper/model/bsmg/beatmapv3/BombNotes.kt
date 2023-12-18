package io.ktlab.bshelper.model.bsmg.beatmapv3

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @see <a href="https://bsmg.wiki/mapping/difficulty-format-v3.html#bomb-notes">bomb notes</a>
 */
@Serializable
data class BombNotes(
    @SerialName("b") val beat: Double,
    @SerialName("x") val x: Int,
    @SerialName("y") val y: Int,
)
