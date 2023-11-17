package io.ktlab.bshelper.model.vo

import io.ktlab.bshelper.model.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class BSPlaylistVO(
    val playlist: BSPlaylist,
    val owner: BSUser,
    val curator: BSUser? = null,
):IPlaylist {
    override val id: String
        get() = owner.id.toString()
    override val title: String
        get() = getName()

    override fun getAvatar(): String {
        return playlist.playlistImage
    }

    override fun getName(): String {
        return playlist.name
    }

    override fun getTotalDuration(): Duration {
        return playlist.totalDuration.toDuration(DurationUnit.SECONDS)
    }

    override fun getMapAmount(): Int {
        return playlist.mapperCount.toInt()
    }

    override fun getAuthor(): String {
        return owner.name
    }

    override fun getBSMaps(): List<IMap> {
        return emptyList()
    }

    override fun getMaxDuration(): Duration {
        return Duration.INFINITE
    }

    override fun getMaxNotes(): Int {
        return Int.MAX_VALUE
    }

    override fun getMaxNPS(): Double {
        return playlist.maxNps
    }

    override fun getAvgDuration(): Duration {
        return Duration.INFINITE
    }

    override fun getAvgNPS(): String {
        return "0"
//        return playlist.avgNps.toString()
    }

    override fun getAvgNotes(): String {
        return "0"
    }

    override fun getImage(): String {
        TODO("Not yet implemented")
    }

    override fun isCustom(): Boolean {
        return false
    }

    override fun getMinNPS(): Double {
        return playlist.minNps
    }

    override fun getTargetPath(): String {
        return ""
    }

}