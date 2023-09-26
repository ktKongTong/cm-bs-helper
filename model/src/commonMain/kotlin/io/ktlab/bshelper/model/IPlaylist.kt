package io.ktlab.bshelper.model

import java.io.File
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

interface IPlaylist {
    val id: String
    val title: String
    fun getName(): String
    fun getTotalDuration(): Duration
    fun getMapAmount(): Int
    fun getAuthor(): String
    fun getBSMaps(): List<IMap>
    fun getMaxDuration(): Duration
    fun getMaxNotes(): Int
    fun getMaxNPS(): Double
    fun getAvgDuration(): Duration
    fun getAvgNPS(): String
    fun getAvgNotes(): String
    fun getImage(): String
    fun isCustom(): Boolean
    fun getMinNPS(): Double
    fun getPlaylistDescription(): String {
        return "nothing here"
    }
    fun getTargetPath():String
}
