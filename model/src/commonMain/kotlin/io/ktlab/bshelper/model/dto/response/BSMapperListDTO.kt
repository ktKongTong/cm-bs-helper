package io.ktlab.bshelper.model.dto.response

import io.ktlab.bshelper.model.dto.BSUserWithStatsDTO
import io.ktlab.bshelper.model.dto.serializer.LocalDateTimeAsStringSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

typealias BSMapperListDTO = List<BSUserWithStatsDTO>

@Serializable
data class BSMapperDetailDTO(
    val admin: Boolean,
    val avatar: String,
    val curator: Boolean,
    val curatorTab: Boolean? = null,
    val description: String,
    val followData: FollowData,
    val id: Int,
    val name: String,
    val patreon: String? = null,
    @SerialName("playlistUrl")
    val playlistURL: String,
    val stats: BSUserStats,
    val type: String,
    val hash: String? = null,
    val verifiedMapper: Boolean? = null,
)

@Serializable
data class FollowData(
    val curation: Boolean,
    val followers: Long,
    val following: Boolean,
    val follows: JsonElement? = null,
    val upload: Boolean,
)

@Serializable
data class BSUserStats(
    val avgBpm: Double,
    val avgDuration: Double,
    val avgScore: Double,
    @Serializable(with = LocalDateTimeAsStringSerializer::class)
    val firstUpload: LocalDateTime,
    @Serializable(with = LocalDateTimeAsStringSerializer::class)
    val lastUpload: LocalDateTime,
    val totalDownvotes: Int = 0,
    val totalMaps: Int = 0,
    val totalUpvotes: Int = 0,
    val diffStats: DiffStats? = null,
)

@Serializable
data class DiffStats(
    val easy: Long,
    val expert: Long,
    val expertPlus: Long,
    val hard: Long,
    val normal: Long,
    val total: Long,
)
