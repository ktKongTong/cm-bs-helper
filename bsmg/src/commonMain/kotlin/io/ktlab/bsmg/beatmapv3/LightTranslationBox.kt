package io.ktlab.bsmg.beatmapv3

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LightTranslationBox(
    @SerialName("b") val beat: Double,
    @SerialName("g") val group: Int,
    @SerialName("e") val events: List<LightTranslationBoxGroupEvent>,
)

@Serializable
data class LightTranslationBoxGroupEvent(
    @SerialName("f") val filterObject: FilterObject,
    @SerialName("w") val beatDistribution: Double,
    @SerialName("d") val beatDistributionType: Int,
    @SerialName("s") val translationDistribution: Double,
    @SerialName("t") val translationDistributionType: Int,
    @SerialName("b") val translationDistributionAffectsFirstEvent: Int,
    @SerialName("i") val translationDistributionEasing: Int,
    @SerialName("a") val axis: Int,
    @SerialName("r") val reverseRotation: Int,
    @SerialName("l") val eventData: List<LightTranslationEventData>,
)

@Serializable
data class LightTranslationEventData(
    @SerialName("b") val beat: Double,
    @SerialName("p") val transitionFromPreviousEventRotationBehaviour: Int,
    @SerialName("e") val easeType: Int,
    @SerialName("t") val translationValue: Double,
)