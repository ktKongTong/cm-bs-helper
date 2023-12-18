package io.ktlab.bshelper.model.bsmg.beatmapv3
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @see <a href="https://bsmg.wiki/mapping/difficulty-format-v3.html#rotation-events">rotation events</a>
 */
@Serializable
data class RotationEvent(
    @SerialName("b") val beat: Double,
    // 0 is Early rotation. Rotates future objects, while also rotating objects at the same time.
    // 1 is Late rotation. Rotates future objects, but ignores rotating objects at the same time.
    @SerialName("e") val eventType: Int,
    @SerialName("r") val rotation: Double,
)
