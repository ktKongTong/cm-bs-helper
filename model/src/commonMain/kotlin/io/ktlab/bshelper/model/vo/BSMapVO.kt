package io.ktlab.bshelper.model.vo

import io.ktlab.bshelper.model.*
import io.ktlab.bshelper.model.enums.ECharacteristic
import io.ktlab.bshelper.model.enums.EMapDifficulty
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class VersionWithDiffList(
    val version: BSMapVersion,
    val diffs: List<MapDifficulty>
)


data class BSMapVO(
    val map: BSMap,
    val uploader: BSUser,
    val versions: List<VersionWithDiffList>
):IMap {

    override fun getSongName(): String {
        return map.name
    }

    override fun getMusicPreviewURL(): String {
        return versions.first().version.previewURL
    }

    fun getDownloadURL(): String {
        return versions.first().version.downloadURL
    }

    override fun getAuthor(): String {
        return uploader.name
    }

    override fun getAvatar(): String {
        return versions.first().version.coverURL
    }

    override fun getMapDescription(): String {
        return map.description?:""
    }

    override fun getDuration(): String {
        return map.duration.toDuration(DurationUnit.SECONDS).toString()
    }

    override fun getID(): String {
        return map.mapId
    }

    override fun getDifficulty(): List<MapDifficulty> {
        TODO()
    }

    override fun getDiffMatrix(): MapDiff {
        return MapDiff.build().addDiff(
            versions.first().diffs.map { it.difficulty }
        )
    }

    override fun getBPM(): String {
        return String.format("%.2f", map.bpm)
    }

    override fun getNotes(): Map<EMapDifficulty, String> {
        return versions.first().diffs.associate { it.difficulty to it.notes.toString() }
    }

    override fun getMaxNotes(): Long {
        return versions.first().diffs.maxOfOrNull { it.notes!! } ?: 0
    }

    override fun getMaxNPS(): Double {
        return versions.first().diffs.maxOfOrNull { it.nps!! } ?: 0.0
    }

    override fun getMapVersion(): String {
        TODO("Not yet implemented")
    }
}