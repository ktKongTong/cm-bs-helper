package io.ktlab.bshelper.model.mapper

import io.ktlab.bshelper.model.*
import io.ktlab.bshelper.model.enums.EMapDifficulty
import io.ktlab.bshelper.model.vo.MapDiff
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
): IMap
{
    override fun getSongName(): String {
        return bsMapWithUploader?.bsMap?.name ?: fsMap.name
    }

    override fun getMusicPreviewURL(): String {
        return ""
//        return Uri.parse(bsMapWithUploader?.versionWithDiffList?.version?.previewURL ?: fsMap.getSongPath())
    }

    override fun getAuthor(): String {
        return bsMapWithUploader?.uploader?.name ?: fsMap.levelAuthorName
    }

    override fun getAvatar(): String {
        return bsMapWithUploader?.version?.coverURL ?: ""
    }


    fun isCurated(): Boolean {
//        return bsMapWithUploader?.uploader?.curatorMapper ?: false
        return false
    }
    fun isVerified(): Boolean {
        return bsMapWithUploader?.uploader?.verifiedMapper ?: false
    }

    fun getUpVotes(): Long? {
        return bsMapWithUploader?.bsMap?.upVotes
    }

    fun getDownVotes(): Long? {
        return bsMapWithUploader?.bsMap?.upVotes
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
            return MapDiff.build()
        }
    }

    override fun getBPM(): String {
        try {
            if (bsMapWithUploader != null) {
                return bsMapWithUploader.bsMap.bpm.toString()
            }
            return "0"
        }catch (e:Exception) {
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
            return mapOf()
        }
    }

    override fun getMaxNotes(): Long {
        try {
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
        return ""
    }

}