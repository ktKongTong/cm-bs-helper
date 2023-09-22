package io.ktlab.bshelper.model.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import io.ktkt.bshelper.data.model.network.serializer.LocalDateTimeAsStringSerializer
import io.ktlab.bshelper.model.enums.EMapDifficulty
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.vo.MapDiff
import io.ktlab.bshelper.model.MapDifficulty

//import io.ktkt.lb.data.model.view.BsMapWithUploader



@Serializable
data class BSMapDTO(
    val id: String,
    val name: String,
    val description: String,
    val bookmarked: Boolean,
    val automapper: Boolean,
    val ranked: Boolean,
    val qualified: Boolean,
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
):IMap {
//    fun covertToDBO() {
//
//    }
    fun convertToDVO() {

    }

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

//    fun convertToBSMapView(): BSMapView {
//        return BSMapView(
//            map = convertToBSEntity(),
//            uploader = uploader.convertToEntity(),
//            versions = versions.map { it.convertToVersionWithDiffList(id) }
//        )
//    }
//    fun convertToBSEntity():BSMap{
//        return BSMap(
//            id = id,
//            name = name,
//            description = description,
//            uploaderId = uploader.id,
//            metadata = metadata.convertToEntity(),
//            stats = stats.convertToEntity(),
//            automapper = automapper,
//            ranked = ranked,
//            qualified = qualified,
//            bookmarked = bookmarked,
//            uploaded = uploaded,
//            tags = tags,
//            createdAt = createdAt,
//            updatedAt = updatedAt,
//            lastPublishedAt = lastPublishedAt
//        )
//    }
//    fun convertToBSWithUploader(): BsMapWithUploader {
//        return BsMapWithUploader(
//            bsMap = convertToBSEntity(),
//            uploader = uploader.convertToEntity(),
//            versionWithDiffList = versions.first().convertToVersionWithDiffList(id)
//        )
//    }
}
