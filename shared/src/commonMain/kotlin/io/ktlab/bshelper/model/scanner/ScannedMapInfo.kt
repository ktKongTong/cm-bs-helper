package io.ktlab.bshelper.model.scanner

import io.beatmaps.common.beatsaber.BSDifficulty
import io.beatmaps.common.beatsaber.BSDifficultyV3
import io.ktlab.bshelper.model.FSMap
import io.ktlab.bshelper.model.MapDifficulty
import io.ktlab.bshelper.model.bsmg.FSMapDifficulty
import io.ktlab.bshelper.model.bsmg.FSMapInfo
import io.ktlab.bshelper.model.enums.ECharacteristic
import io.ktlab.bshelper.model.enums.EMapDifficulty
import okio.Path
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun BSDifficulty.generateMapDifficultyInfo(
    extractedMapInfo: IExtractedMapInfo,
    characteristic: ECharacteristic,
    difficulty: FSMapDifficulty,
): MapDifficulty {
    when (extractedMapInfo) {
        is IExtractedMapInfo.LocalMapInfo, is IExtractedMapInfo.BSMapInfo -> {
            return MapDifficulty(
                seconds = 0.0,
                hash = extractedMapInfo.hash,
                mapId = if (extractedMapInfo is IExtractedMapInfo.BSMapInfo) extractedMapInfo.mapId else "",
                difficulty = EMapDifficulty.from(difficulty.difficulty),
                characteristic = characteristic,
                notes = noteCount().toLong(),
                nps = 0.0,
                njs = 0.0,
                bombs = bombCount().toLong(),
                obstacles = obstacleCount().toLong(),
                offset = difficulty.noteJumpStartBeatOffset,
                events = eventCount().toLong(),
                length = 0.0,
                chroma = null,
                me = null,
                ne = null,
                cinema = null,
                maxScore = null,
                label = null,
            )
        }
        else -> throw Exception("Unknown map info type")
    }
}

fun BSDifficultyV3.generateMapDifficultyInfo(
    extractedMapInfo: IExtractedMapInfo,
    characteristic: ECharacteristic,
    difficulty: FSMapDifficulty,
): MapDifficulty {
    when (extractedMapInfo) {
        is IExtractedMapInfo.LocalMapInfo, is IExtractedMapInfo.BSMapInfo -> {
            return MapDifficulty(
                seconds = 0.0,
                hash = extractedMapInfo.hash,
                mapId = if (extractedMapInfo is IExtractedMapInfo.BSMapInfo) extractedMapInfo.mapId else "",
                difficulty = EMapDifficulty.from(difficulty.difficulty),
                characteristic = characteristic,
                notes = noteCount().toLong(),
                nps = 0.0,
                njs = 0.0,
                bombs = bombCount().toLong(),
                obstacles = obstacleCount().toLong(),
                offset = difficulty.noteJumpStartBeatOffset,
                events = eventCount().toLong(),
                length = 0.0,
                chroma = null,
                me = null,
                ne = null,
                cinema = null,
                maxScore = null,
                label = null,
            )
        }
        else -> throw Exception("Unknown map info type")
    }
}

sealed interface IExtractedMapInfo {
    val hash: String

    // means that the map is not uploaded to beatsaver
    data class LocalMapInfo(
        override val hash: String,
        val mapPath: Path,
        val name: String,
        val infoFilename: String? = null,
        val mapInfo: FSMapInfo,
        val v2MapObjectMap: Map<String, BSDifficulty>? = null,
        val v3MapObjectMap: Map<String, BSDifficultyV3>? = null,
    ) : IExtractedMapInfo {
        fun generateMapDifficultyInfo(): List<MapDifficulty> {
            val difficultyDBOList = mutableListOf<MapDifficulty>()
            mapInfo.difficultyBeatmapSets.forEach { bms ->
                bms.difficultyBeatmaps.forEach { bf ->
                    v2MapObjectMap?.get(bms.characteristicName + bf.difficulty)?.let {
                        difficultyDBOList += it.generateMapDifficultyInfo(this, ECharacteristic.from(bms.characteristicName), bf)
                    }
                    v3MapObjectMap?.get(bms.characteristicName + bf.difficulty)?.let {
                        difficultyDBOList += it.generateMapDifficultyInfo(this, ECharacteristic.from(bms.characteristicName), bf)
                    }
                }
            }
            return difficultyDBOList
        }

        fun generateFSMapDBO(playlistId: String): FSMap {
            return FSMap(
                hash = hash,
                name = name,
                duration = Duration.ZERO,
                previewStartTime = mapInfo.previewStartTime,
                previewDuration = mapInfo.previewDuration.toDuration(DurationUnit.SECONDS),
                bpm = mapInfo.bpm,
                songName = mapInfo.songName,
                songSubname = mapInfo.songSubName,
                songAuthorName = mapInfo.songAuthorName,
                levelAuthorName = mapInfo.levelAuthorName,
                relativeCoverFilename = mapInfo.coverFilename,
                relativeSongFilename = mapInfo.songFilename,
                relativeInfoFilename = infoFilename ?: "",
                dirName = mapPath.name,
                playlistBasePath = mapPath.parent.toString(),
                playlistId = playlistId,
                active = true,
                mapId = "",
            )
        }
    }

    data class BSMapInfo(
        override val hash: String,
        val mapId: String,
        val mapPath: Path,
        val mapInfo: FSMapInfo,
        val name: String,
        val infoFilename: String? = null,
        val v2MapObjectMap: Map<String, BSDifficulty>? = null,
        val v3MapObjectMap: Map<String, BSDifficultyV3>? = null,
    ) : IExtractedMapInfo {
        fun generateMapDifficultyInfo(): List<MapDifficulty> {
            val difficultyDBOList = mutableListOf<MapDifficulty>()
            mapInfo.difficultyBeatmapSets.forEach { bms ->
                bms.difficultyBeatmaps.forEach { bf ->
                    v2MapObjectMap?.get(bms.characteristicName + bf.difficulty)?.let {
                        difficultyDBOList += it.generateMapDifficultyInfo(this, ECharacteristic.from(bms.characteristicName), bf)
                    }
                    v3MapObjectMap?.get(bms.characteristicName + bf.difficulty)?.let {
                        difficultyDBOList += it.generateMapDifficultyInfo(this, ECharacteristic.from(bms.characteristicName), bf)
                    }
                }
            }
            return difficultyDBOList
        }

        fun generateFSMapDBO(playlistId: String): FSMap {
            return FSMap(
                hash = hash,
                name = name,
                duration = Duration.ZERO,
                previewStartTime = mapInfo.previewStartTime,
                previewDuration = mapInfo.previewDuration.toDuration(DurationUnit.SECONDS),
                bpm = mapInfo.bpm,
                songName = mapInfo.songName,
                songSubname = mapInfo.songSubName,
                songAuthorName = mapInfo.songAuthorName,
                levelAuthorName = mapInfo.levelAuthorName,
                relativeCoverFilename = mapInfo.coverFilename,
                relativeSongFilename = mapInfo.songFilename,
                relativeInfoFilename = infoFilename ?: "",
                dirName = mapPath.name,
                playlistBasePath = mapPath.parent.toString(),
                playlistId = playlistId,
                active = true,
                mapId = mapId,
            )
        }
    }

    data class ErrorMapInfo(
        override val hash: String,
        val mapId: String?,
        val mapPath: Path,
        val mapInfo: FSMapInfo?,
        val exception: ScannerException,
    ) : IExtractedMapInfo {
        fun generateMapDifficultyInfo(): List<MapDifficulty> {
            return emptyList()
        }

        fun generateFSMapDBO(playlistId: String): FSMap {
//            when (exception) {
//                is ScannerException.JSONFileTooLargeException
//            }
            return FSMap(
                hash = hash,
                name = mapInfo?.songName ?: "",
                duration = Duration.ZERO,
                previewStartTime = mapInfo?.previewStartTime ?: 0.0,
                previewDuration = mapInfo?.previewDuration?.toDuration(DurationUnit.SECONDS) ?: Duration.ZERO,
                bpm = mapInfo?.bpm ?: 0.0,
                songName = mapInfo?.songName ?: "",
                songSubname = "mapInfo.songSubname",
                songAuthorName = mapInfo?.songAuthorName ?: "",
                levelAuthorName = "mapInfo.levelAuthorName",
                relativeCoverFilename = "",
                relativeSongFilename = "",
                relativeInfoFilename = "",
                dirName = mapPath.name,
                playlistBasePath = mapPath.parent.toString(),
                playlistId = playlistId,
                active = true,
                mapId = mapId ?: "",
            )
        }
    }
}
