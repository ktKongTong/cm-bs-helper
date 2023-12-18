package io.ktlab.bshelper.model.vo

import io.ktlab.bshelper.model.BSMap
import io.ktlab.bshelper.model.BSMapVersion
import io.ktlab.bshelper.model.BSUser
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.MapDifficulty
import io.ktlab.bshelper.model.enums.EMapDifficulty
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class VersionWithDiffList(
    val version: BSMapVersion,
    val diffs: List<MapDifficulty>,
)

data class BSMapVO(
    val map: BSMap,
    val uploader: BSUser,
    val curator: BSUser? = null,
    val collaborators: List<BSUser>? = null,
    val versions: List<VersionWithDiffList>,
) : IMap {
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
        return map.description ?: ""
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
            versions.first().diffs.map { it.difficulty },
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

    override fun getAuthorAvatar(): String {
        return uploader.avatar
    }

    override fun isRelateWithBSMap(): Boolean {
        return true
    }

    fun getFilename(): String {
        return "${getID()} (${map.songName} - ${map.songAuthorName})"
    }
}
