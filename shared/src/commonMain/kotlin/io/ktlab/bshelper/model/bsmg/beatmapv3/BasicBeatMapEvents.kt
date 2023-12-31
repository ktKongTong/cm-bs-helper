package io.ktlab.bshelper.model.bsmg.beatmapv3

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BasicBeatMapEvents(
    @SerialName("b") val beat: Double,
    @SerialName("et") val eventType: Int,
    @SerialName("i") val value: Int,
    @SerialName("f") val floatValue: Double,
)
