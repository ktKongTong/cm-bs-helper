package io.ktlab.bshelper.model.dto.request

import io.ktlab.bshelper.model.annotation.BSMapFeatTag
import io.ktlab.bshelper.model.annotation.QueryParam
import io.ktlab.bshelper.model.dto.serializer.DateAsStringSerializer
import io.ktlab.bshelper.model.enums.MapFeatureTag
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

data class PlaylistFilterParam(
    @QueryParam("q") val queryKey: String = "",
    @QueryParam("maxNps") val maxNps: Double? = null,
    @QueryParam("minNps") val minNps: Double? = null,
    @Serializable(with = DateAsStringSerializer::class)
    @QueryParam("from") val from: LocalDate? = null,
    @Serializable(with = DateAsStringSerializer::class)
    @QueryParam("to") val to: LocalDate? = null,
    @BSMapFeatTag(MapFeatureTag.Curated)
    @QueryParam("curated") val curated: Boolean? = null,
    @BSMapFeatTag(MapFeatureTag.VerifiedMapper)
    @QueryParam("verified") val verified: Boolean? = null,
    @QueryParam("includeEmpty") val includeEmpty: Boolean? = null,
    @QueryParam("sortOrder") val sortKey: String = "Relevance",
) {
    fun mapFeatureTagsMap(): Map<MapFeatureTag, Boolean> {
        return mapOf(
            MapFeatureTag.Curated to curated,
            MapFeatureTag.VerifiedMapper to verified,
        ).filterValues { it != null } as Map<MapFeatureTag, Boolean>
    }

    companion object {
        val default = PlaylistFilterParam()
    }
//    fun copyWithFeatureTag(tag: MapFeatureTag, value: Boolean?,vararg ) : PlaylistFilterParam {
//        return when (tag) {
//            MapFeatureTag.Curated -> this.copy(curated = value)
//            MapFeatureTag.VerifiedMapper -> this.copy(verified = value)
//            else -> this
//        }
//    }
}
