package io.ktlab.bshelper.model.dto

import io.ktlab.bshelper.model.BSMap
import io.ktlab.bshelper.model.dto.serializer.LocalDateTimeAsStringSerializer
import io.ktlab.bshelper.model.vo.BSMapVO
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable


@Serializable
data class BSMapDTO(
    val id: String,
    val name: String,
    val description: String,
    val bookmarked: Boolean=false,
    val automapper: Boolean,
    val ranked: Boolean,
    val qualified: Boolean,
    val collaborators: List<BSUserDTO>? = null,
    val curator: BSUserDTO? = null,
    val uploader: BSUserDTO,
    val metadata: MapMetadataDTO,
    val stats: MapStatsDTO,
    val versions: List<MapVersionDTO>,
    val tags: List<String> = listOf(),
    @Serializable(with = LocalDateTimeAsStringSerializer::class)
    val uploaded: LocalDateTime,
    @Serializable(with = LocalDateTimeAsStringSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeAsStringSerializer::class)
    val updatedAt: LocalDateTime,
    @Serializable(with = LocalDateTimeAsStringSerializer::class)
    val lastPublishedAt: LocalDateTime
) {

    fun convertToVO():BSMapVO {
        return BSMapVO(
            map = BSMap(
                mapId = id,
                name = name,
                description = description,
                uploaderId = uploader.id,
//                curatorId = curator?.id?.toLong(),
//                collaboratorIds = collaborators?.map { it.id.toLong() },
                automapper = automapper,
                ranked = ranked,
                qualified = qualified,
                bookmarked = bookmarked,
                uploaded = uploaded,
                tags = tags,
                createdAt = createdAt,
                updatedAt = updatedAt,
                lastPublishedAt = lastPublishedAt,
                plays = stats.plays,
                downloads = stats.downloads,
                upVotes = stats.upvotes,
                downVotes = stats.downvotes,
                score = stats.score,
                bpm = metadata.bpm,
                duration = metadata.duration,
                songName = metadata.songName,
                songSubname = metadata.songSubName,
                songAuthorName = metadata.songAuthorName,
                levelAuthorName = metadata.levelAuthorName
            ),
            uploader = uploader.convertToEntity(),
            curator = curator?.convertToEntity(),
            collaborators = collaborators?.map { it.convertToEntity() },
            versions = versions.map { it.convertToVersionWithDiffList(id) }
        )
    }
}
