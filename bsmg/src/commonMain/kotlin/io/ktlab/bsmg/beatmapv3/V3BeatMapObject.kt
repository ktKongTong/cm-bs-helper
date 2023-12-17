package io.ktlab.bsmg.beatmapv3

import io.ktlab.bsmg.CustomData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

/**
 * @see <a href="https://bsmg.wiki/mapping/difficulty-format-v3.html#v3-base-object">difficulty format v3</a>
 */
@Serializable
data class V3BeatMapObject(
    val version: String,
    val basicBeatmapEvents: List<BasicBeatMapEvents>,
    val bombNotes: List<BombNotes>,
    val bpmEvents: List<BPMEvent>,
    val burstSliders: List<BurstSliders>,
    val colorBoostBeatmapEvents: List<ColorBoostBeatmapEvents>,
    val colorNotes: List<ColorNotes>,
    val lightColorEventBoxGroups: List<LightColorEventBox>,
    val lightRotationEventBoxGroups: List<LightRotationBox>,
    val lightTranslationEventBoxGroups: List<LightTranslationBox>,
    val obstacles: List<Obstacles>,
    val rotationEvents: List<RotationEvent>,
    val sliders: List<Sliders>,
    val useNormalEventsAsCompatibleEvents: Boolean,
    val waypoints: JsonArray,
    val basicEventTypesWithKeywords: CustomData = null,
    val customData: CustomData = null,
)
