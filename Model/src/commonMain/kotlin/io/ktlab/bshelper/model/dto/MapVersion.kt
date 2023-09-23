package io.ktlab.bshelper.model.dto

import io.ktlab.bshelper.model.dto.serializer.LocalDateTimeAsStringSerializer
import io.ktlab.bshelper.model.BSMapVersion
import io.ktlab.bshelper.model.vo.VersionWithDiffList
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MapVersionDTO (
    val hash: String,
    val key: String? = "none",
    val state: String,
    @Serializable(with = LocalDateTimeAsStringSerializer::class)
    val createdAt: LocalDateTime,
    val sageScore: Long? = 0,
    val diffs: List<MapDiffDTO>,
    val downloadURL: String,
    val coverURL: String,
    val previewURL: String,
){



    fun convertToVersionWithDiffList(mapId:String): VersionWithDiffList {
        return VersionWithDiffList(
            version = convertToEntity(mapId),
            diffs = diffs.map { it.convertToEntity(hash,mapId) }
        )
    }
    fun convertToEntity(mapId:String): BSMapVersion {
        return BSMapVersion(
            hash = hash,
            key = key?:"none",
            state = state,
            createdAt = createdAt,
            sageScore = sageScore?:0,
//            diffs = diffs.map { it.convertToEntity() },
            downloadURL = downloadURL,
            coverURL = coverURL,
            previewURL = previewURL,
            mapId = mapId,
        )
    }
}