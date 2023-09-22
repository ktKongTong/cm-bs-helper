package io.ktlab.bsmg.beatmapv3

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LightColorEventBox(
    @SerialName("b") val beat: Double,
    @SerialName("g") val group: Int,
    @SerialName("e") val events: List<LightColorEvent>,
)

@Serializable
data class LightColorEvent(
    @SerialName("f") val beat: Double,
    @SerialName("w") val beatDistribution: Double,
    @SerialName("d") val beatDistributionType: Int,
    @SerialName("r") val brightnessDistribution: Double,
    @SerialName("t") val brightnessDistributionType: Int,
    @SerialName("b") val brightnessDistributionAffectsFirstEvent: Int,
    @SerialName("i") val brightnessDistributionEasing: Int,
    @SerialName("e") val eventData: List<LightColorEventData>,
)

@Serializable
data class LightColorEventData(
    @SerialName("b") val beat: Double,
    @SerialName("i") val transitionType: Int,
    @SerialName("c") val color: Int,
    @SerialName("s") val brightness: Double,
    @SerialName("f") val flickerFrequency: Int,
)