package io.ktlab.bsmg.beatmapv2

import io.ktlab.bsmg.CustomData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

/**
 * @see <a href="https://bsmg.wiki/mapping/difficulty-format-v2.html#v2-base-object">difficulty format v2</a>
 */
@Serializable
data class V2BeatMapObject(
    val _version: String,
    val _notes: List<Note>,
    val _sliders: List<Sliders>? = null,
    val _obstacles: List<Obstacles>,
    val _events: List<Events>,
    val _waypoints: List<JsonArray>? = null,
    val _customData: CustomData = null,
)

@Serializable
data class Note(
    val _time: Double,
    val _lineIndex: Int,
    val _lineLayer: Int,
    //    0: red, 1: blue, 2: Unused, 3: bomb
    val _type: Int,
    // 0: up, 1: down, 2: left, 3: right, 4: up-left, 5: up-right, 6: down-left, 7: down-right, 8: dot
    val _cutDirection: Int,
    val _customData: CustomData = null,
)

@Serializable
data class Sliders(
    val _colorType: Int,
    val _headTime: Double,
    val _headLineIndex: Int,
    val _headLineLayer: Int,
    val _headControlPointLengthMultiplier: Double,
    val _headCutDirection: Int,
    val _tailTime: Double,
    val _tailLineIndex: Int,
    val _tailLineLayer: Int,
    val _tailControlPointLengthMultiplier: Double,
    val _tailCutDirection: Int,
    val _sliderMidAnchorMode: Int,
    val _customData: CustomData = null,
)

@Serializable
data class Obstacles(
    val _time: Double,
    val _lineIndex: Int,
    val _type: Int,
    val _duration: Double,
    val _width: Int,
    val _customData: CustomData = null,
)

@Serializable
data class Events(
    val _time: Double,
    val _type: Int,
    val _value: Int,
    val _floatValue: Double? = null,
    val _customData: CustomData = null,
)
