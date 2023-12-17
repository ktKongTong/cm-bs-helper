package io.ktlab.bsmg

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FSMapInfo(
    @SerialName("_version") val version: String,
    @SerialName("_songAuthorName") val songAuthorName: String,
    @SerialName("_songFilename") val songFilename: String,
    @SerialName("_coverImageFilename") val coverFilename: String,
    @SerialName("_songName") val songName: String,
    @SerialName("_beatsPerMinute") val bpm: Double,
    @SerialName("_previewStartTime") val previewStartTime: Double,
    @SerialName("_previewDuration") val previewDuration: Double,
    @SerialName("_environmentName") val environmentName: String,
    @SerialName("_difficultyBeatmapSets") val difficultyBeatmapSets: List<FSMapDiffSet>,
)

@Serializable
data class FSMapDiffSet(
    @SerialName("_beatmapCharacteristicName") val characteristicName: String,
    @SerialName("_difficultyBeatmaps") val difficultyBeatmaps: List<FSMapDifficulty>,
)

@Serializable
data class FSMapDifficulty(
    @SerialName("_difficulty") val difficulty: String,
    @SerialName("_difficultyRank") val difficultyRank: Int,
    @SerialName("_beatmapFilename") val beatmapFilename: String,
    @SerialName("_noteJumpMovementSpeed") val noteJumpMovementSpeed: Double,
    @SerialName("_noteJumpStartBeatOffset") val noteJumpStartBeatOffset: Double,
)
