package io.ktlab.bshelper.model.mapper

import io.ktlab.bshelper.model.BSMap
import io.ktlab.bshelper.model.BSMapVersion
import io.ktlab.bshelper.model.BSUser
import io.ktlab.bshelper.model.FSMap
import io.ktlab.bshelper.model.GetAllByPlaylistId
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.MapDifficulty
import io.ktlab.bshelper.model.enums.ECharacteristic
import io.ktlab.bshelper.model.enums.EMapDifficulty
import io.ktlab.bshelper.model.vo.MapDiff
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration

data class BsMapWithUploader(
    val bsMap: BSMap,
    val uploader: BSUser? = null,
    val version: BSMapVersion? = null,
    val difficulties: List<MapDifficulty>? = null,
)
class FSMapVO(
val fsMap: FSMap,
val difficulties: List<MapDifficulty>? = null,
val bsMapWithUploader: BsMapWithUploader? = null,
): IMap {
    override fun getSongName(): String {
        TODO("Not yet implemented")
    }

    override fun getMusicPreviewURL(): String {
        TODO("Not yet implemented")
    }

    override fun getAuthor(): String {
        TODO("Not yet implemented")
    }

    override fun getAvatar(): String {
        TODO("Not yet implemented")
    }

    override fun getMapDescription(): String {
        TODO("Not yet implemented")
    }

    override fun getDuration(): String {
        TODO("Not yet implemented")
    }

    override fun getID(): String {
        TODO("Not yet implemented")
    }

    override fun getDifficulty(): List<MapDifficulty> {
        TODO("Not yet implemented")
    }

    override fun getDiffMatrix(): MapDiff {
        TODO("Not yet implemented")
    }

    override fun getBPM(): String {
        TODO("Not yet implemented")
    }

    override fun getNotes(): Map<EMapDifficulty, String> {
        TODO("Not yet implemented")
    }

    override fun getMaxNotes(): Long {
        TODO("Not yet implemented")
    }

    override fun getMaxNPS(): Double {
        TODO("Not yet implemented")
    }

    override fun getMapVersion(): String {
        TODO("Not yet implemented")
    }

}

val getAllByPlaylistIdQueryMapper = fun (
    mapId: String,
    version: String?,
    name: String,
    author: String,
    duration: Duration,
    relativeCoverPath: String,
    relativeSongPath: String,
    relativeInfoPath: String,
    dirFilename: String,
    playlistBasePath: String,
    hash: String?,
    playlistId: String,
    mapId_: String?,
    name_: String?,
    description: String?,
    uploaderId: Long?,
    bpm: Double?,
    duration_: Long?,
    songName: String?,
    songSubname: String?,
    songAuthorName: String?,
    levelAuthorName: String?,
    plays: Long?,
    downloads: Long?,
    upVotes: Long?,
    downVotes: Long?,
    score: Double?,
    automapper: Boolean?,
    ranked: Boolean?,
    qualified: Boolean?,
    bookmarked: Boolean?,
    uploaded: LocalDateTime?,
    tags: List<String>?,
    createdAt: LocalDateTime?,
    updatedAt: LocalDateTime?,
    lastPublishedAt: LocalDateTime?,
    id: Int?,
    name__: String?,
    avatar: String?,
    description_: String?,
    type: String?,
    admin: Boolean?,
    curator: Boolean?,
    playlistUrl: String?,
    uuid: String?,
    seconds: Double?,
    hash_: String?,
    mapId__: String?,
    difficulty: EMapDifficulty?,
    characteristic: ECharacteristic?,
    notes: Long?,
    nps: Double?,
    njs: Double?,
    bombs: Long?,
    obstacles: Long?,
    offset: Double?,
    events: Long?,
    chroma: Boolean?,
    length: Double?,
    me: Boolean?,
    ne: Boolean?,
    cinema: Boolean?,
    maxScore: Long?,
    label: String?,
):FSMapVO{
    TODO()
//    return FSMapVO()
}