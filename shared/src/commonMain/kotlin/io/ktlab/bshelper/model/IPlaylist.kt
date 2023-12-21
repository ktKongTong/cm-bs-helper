package io.ktlab.bshelper.model

import kotlin.time.Duration

interface IPlaylist {
    val id: String
    val title: String
    fun getMangerFolderId(): Long
    fun getAvatar(): String

    fun getName(): String

    fun getTotalDuration(): Duration

    fun getMapAmount(): Int

    fun getAuthor(): String

    fun getBSMaps(): List<io.ktlab.bshelper.model.IMap>

    fun getMaxDuration(): Duration

    fun getMaxNotes(): Int

    fun getMaxNPS(): Double

    fun getAvgDuration(): Duration

    fun getAvgNPS(): Double

    fun getAvgNotes(): Double

    fun getImage(): String

    fun isCustom(): Boolean

    fun getMinNPS(): Double

    fun getPlaylistDescription(): String

    fun getTargetPath(): String
}
