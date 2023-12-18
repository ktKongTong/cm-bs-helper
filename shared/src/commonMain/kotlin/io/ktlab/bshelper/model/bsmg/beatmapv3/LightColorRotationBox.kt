package io.ktlab.bshelper.model.bsmg.beatmapv3

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LightRotationBox(
    @SerialName("b") val beat: Double,
    @SerialName("g") val group: Int,
    @SerialName("e") val events: List<LightRotationEvent>,
)

@Serializable
data class LightRotationEvent(
    @SerialName("f") val filterObject: FilterObject,
    @SerialName("w") val beatDistribution: Double,
    @SerialName("d") val beatDistributionType: Int,
    @SerialName("s") val rotationDistribution: Double,
    @SerialName("t") val rotationDistributionType: Int,
    @SerialName("b") val rotationDistributionAffectsFirstEvent: Int,
    @SerialName("i") val rotationDistributionEasing: Int,
    @SerialName("a") val axis: Int,
    @SerialName("r") val reverseRotation: Int,
    @SerialName("l") val eventData: List<LightRotationEventData>,
)

@Serializable
data class LightRotationEventData(
    @SerialName("b") val beat: Double,
    @SerialName("p") val transitionFromPreviousEventRotationBehaviour: Int,
    @SerialName("e") val easeType: Int,
    @SerialName("l") val additionalLoops: Int,
    @SerialName("r") val rotationValue: Double,
    @SerialName("o") val rotationDirection: Int,
)
