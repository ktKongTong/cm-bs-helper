package io.ktlab.bshelper.model.bsmg.beatmapv3

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FilterObject(
    @SerialName("c") val chunks: Int,
    @SerialName("f") val filterType: Int,
    @SerialName("p") val parameter0: Int,
    @SerialName("t") val parameter1: Int,
    @SerialName("r") val reverse: Int,
    @SerialName("n") val randomBehaviour: Int,
    @SerialName("s") val randomSeed: Int,
    @SerialName("l") val limitPercentage: Double,
    @SerialName("d") val limitBehaviour: Int,
)
