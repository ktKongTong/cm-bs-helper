package io.ktlab.bshelper.model.dto

import io.ktlab.bshelper.model.BSPlaylist
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.dto.serializer.LocalDateTimeAsStringSerializer
import io.ktlab.bshelper.model.vo.BSPlaylistVO
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class BSPlaylistDTO (
    val createdAt: String,
    val description: String,
    val downloadURL: String,
    val name: String,
    val owner: BSUserDTO,
    val playlistId: Int,
    val playlistImage: String,
    val playlistImage512: String,
    val songsChangedAt: String? = null,
    val stats: PlaylistStats,
    val type: String,
    val updatedAt: String,
)

@Serializable
enum class PlaylistType(val value:String) {
    Private("Private"),
    Public("Public"),
    System("System"),
    Search("Search"),
}

@Serializable
data class BSPlaylistFullDTO (
    @Serializable(with = LocalDateTimeAsStringSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeAsStringSerializer::class)
    val curatedAt: LocalDateTime? = null,
    val description: String,
    val downloadURL: String,
    @SerialName("name")
    val _name: String,
    val curator: BSUserDTO? = null,

    val owner: BSUserDTO,
    val playlistId: Int,
    val playlistImage: String,
    val playlistImage512: String,
//    @Serializable(with = LocalDateTimeAsStringSerializer::class)
//    val songsChangedAt: LocalDateTime? = null,
//    @Serializable(with = LocalDateTimeAsStringSerializer::class)
//    val deleteAt: LocalDateTime? = null,
    val stats: PlaylistStats? = null,
    val type: PlaylistType,
    @Serializable(with = LocalDateTimeAsStringSerializer::class)
    val updatedAt: LocalDateTime,
):IPlaylist{

    fun convertToVO():BSPlaylistVO {
        return BSPlaylistVO(
            BSPlaylist(
                id = playlistId,
                name = _name,
                description = description,
                ownerId = owner.id,
                curatorId = curator?.id,
                createdAt = createdAt,
                updatedAt = updatedAt,
                playlistImage = playlistImage,
                playlistImage512 = playlistImage512,
                downloadURL = downloadURL,
                type = type.value,
                avgScore = stats?.avgScore?:0.0,
                upVotes = stats?.upVotes?:0,
                downVotes = stats?.downVotes?:0,
                mapperCount = stats?.mapperCount?:0,
                maxNps = stats?.maxNps?:0.0,
                minNps = stats?.minNps?:0.0,
                totalDuration = stats?.totalDuration?:0,
                songsChangedAt = null
            ),owner.convertToEntity(),curator?.convertToEntity()
        )
    }

    override val id: String
        get() = playlistId.toString()
    override val title: String
        get() = _name

    override fun getAvatar(): String {
        TODO("Not yet implemented")
    }

    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun getTotalDuration(): Duration {
        TODO("Not yet implemented")
    }

    override fun getMapAmount(): Int {
        TODO("Not yet implemented")
    }

    override fun getAuthor(): String {
        TODO("Not yet implemented")
    }

    override fun getBSMaps(): List<IMap> {
        TODO("Not yet implemented")
    }

    override fun getMaxDuration(): Duration {
        TODO("Not yet implemented")
    }

    override fun getMaxNotes(): Int {
        TODO("Not yet implemented")
    }

    override fun getMaxNPS(): Double {
        TODO("Not yet implemented")
    }

    override fun getAvgDuration(): Duration {
        TODO("Not yet implemented")
    }

    override fun getAvgNPS(): String {
        TODO("Not yet implemented")
    }

    override fun getAvgNotes(): String {
        TODO("Not yet implemented")
    }

    override fun getImage(): String {
        TODO("Not yet implemented")
    }

    override fun isCustom(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getMinNPS(): Double {
        TODO("Not yet implemented")
    }

    override fun getTargetPath(): String {
        TODO("Not yet implemented")
    }

}