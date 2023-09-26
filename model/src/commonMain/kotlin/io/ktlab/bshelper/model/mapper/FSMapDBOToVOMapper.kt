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
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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
        return bsMapWithUploader?.bsMap?.name ?: fsMap.name
    }

    override fun getMusicPreviewURL(): String {
        return ""
//        return Uri.parse(bsMapWithUploader?.versionWithDiffList?.version?.previewURL ?: fsMap.getSongPath())
    }

    override fun getAuthor(): String {
        return bsMapWithUploader?.uploader?.name ?: fsMap.author
    }

    override fun getAvatar(): String {
        return ""
//        return bsMapWithUploader?.versionWithDiffList?.version?.coverURL ?: fsMap.getCoverPath().ifEmpty { "" }
    }

    fun getAuthorAvatar(): String {
        return ""
//        return bsMapWithUploader?.uploader?.avatar ?: fsMap.getCoverPath().ifEmpty { "" }
    }

    override fun getMapDescription(): String {
        return bsMapWithUploader?.bsMap?.description ?: ""
    }

    override fun getDuration(): String {
        try {
            if (bsMapWithUploader != null) {
                return bsMapWithUploader.bsMap.duration.toDuration(DurationUnit.MILLISECONDS).toString()
            }

            return fsMap.duration.toString()
        }catch (e:Exception) {
//            Log.e("FSMapView",(e.message?:"") + this.toString())
            return "0"
        }
    }

    override fun getID(): String {
        return fsMap.mapId
    }

    override fun getDifficulty(): List<MapDifficulty> {
        if (difficulties != null) {
            return difficulties
        }
        return listOf()
    }

    override fun getDiffMatrix(): MapDiff {
        try {
            if (difficulties == null) {
                return MapDiff.build()
            }
            return MapDiff.build().addDiff(
                difficulties.map { it.difficulty }
            )
        }catch (e:Exception) {
//            Log.e("FSMapView",(e.message?:"") + this.toString())
            return MapDiff.build()
        }
    }

    override fun getBPM(): String {
        try {
            if (bsMapWithUploader != null) {
                return bsMapWithUploader!!.bsMap.bpm.toString()
            }
            return "0"
        }catch (e:Exception) {
//            Log.e("FSMapView",(e.message?:"") + this.toString())
            return "0"
        }
    }

    override fun getNotes(): Map<EMapDifficulty, String> {
        try {
            if (difficulties == null) {
                return mapOf()
            }
            return difficulties.associate { it.difficulty to it.notes.toString() }
        }catch (e:Exception) {
//            Log.e("FSMapView",(e.message?:"") + this.toString())
            return mapOf()
        }
    }

    override fun getMaxNotes(): Long {
        try {
//            if (bsMapWithUploader != null) {
//                return bsMapWithUploader.version!!.diffs.maxOf { it.notes }
//            }
            if (difficulties != null) {
                return difficulties.maxOf { it.notes!! }
            }
            return 0
        } catch (e:Exception) {
//            Log.e("FSMapView",(e.message?:"") + this.toString())
            return 0
        }
    }

    override fun getMaxNPS(): Double {
        try {
//            if (bsMapWithUploader != null) {
//                return bsMapWithUploader!!.versionWithDiffList!!.diffs.maxOf { it.notePerSecond }
//            }
            if (difficulties != null) {
                return difficulties.maxOf { it.nps!! }
            }
        }catch (e:Exception) {
//            Log.e("FSMapView",(e.message?:"") + this.toString())
        }
        return 0.0
    }

    override fun getMapVersion(): String {
        return fsMap.version!!
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