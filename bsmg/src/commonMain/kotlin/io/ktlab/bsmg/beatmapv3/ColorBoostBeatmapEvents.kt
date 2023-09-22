package io.ktlab.bsmg.beatmapv3

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ColorBoostBeatmapEvents(
    @SerialName("b") val beat: Double,
    @SerialName("o") val open: Boolean,
)
