package io.ktlab.bshelper.model

import io.ktlab.bshelper.model.enums.EMapDifficulty
import io.ktlab.bshelper.model.vo.MapDiff


interface IMap {
    fun getSongName(): String
    fun getMusicPreviewURL(): String
    fun getAuthor(): String
    fun getAvatar(): String
    fun getAuthorAvatar(): String
    fun getMapDescription(): String
    fun getDuration(): String
    fun getID(): String
    fun getDifficulty(): List<MapDifficulty>
    fun getDiffMatrix(): MapDiff
    fun getBPM(): String
    fun getNotes(): Map<EMapDifficulty,String>
    fun getMaxNotes(): Long
    fun getMaxNPS(): Double
    fun getMapVersion(): String
    fun isRelateWithBSMap(): Boolean
}