package io.ktlab.bshelper.model

import io.ktlab.bshelper.model.enums.EMapDifficulty
import io.ktlab.bshelper.model.vo.MapDiff
import kotlin.time.Duration


interface IMap {
    fun getSongName(): String
    fun getMusicPreviewURL(): String
    fun getAuthor(): String
    fun getAvatar(): String
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
}


fun BSMap.toFSMap(targetPlaylist:FSPlaylist):FSMap {
    return FSMap(
        mapId = mapId,
        version = null,
        name = name,
        author = this.songAuthorName,
        duration = Duration.ZERO,
        relativeCoverPath = "",
        relativeSongPath = "",
        relativeInfoPath = "",
        dirFilename = "$mapId ($songName - $songAuthorName)".replace("/", " "),
        playlistBasePath = targetPlaylist.basePath,
        hash = null,
        playlistId = targetPlaylist.uuid,
    )
}