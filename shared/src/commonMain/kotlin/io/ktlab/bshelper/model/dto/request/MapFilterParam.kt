package io.ktlab.bshelper.model.dto.request

import io.ktlab.bshelper.model.annotation.BSMapFeatTag
import io.ktlab.bshelper.model.annotation.QueryParam
import io.ktlab.bshelper.model.dto.serializer.DateAsStringSerializer
import io.ktlab.bshelper.model.enums.MapFeatureTag
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

data class MapFilterParam(
    @QueryParam("q") val queryKey: String = "",
    // Tag query, separated by , (and) or | (or). Excluded tags are prefixed with !.
    @QueryParam("tags") val tags: String? = null,
    @QueryParam("maxNps") val maxNps: Double? = null,
    @QueryParam("minNps") val minNps: Double? = null,
    @QueryParam("maxDuration") val maxDuration: Int? = null,
    @QueryParam("minDuration") val minDuration: Int? = null,
    @QueryParam("minBpm") val minBpm: Float? = null,
    @QueryParam("maxBpm") val maxBpm: Float? = null,
    @QueryParam("minRating") val minRating: Float? = null,
    @QueryParam("maxRating") val maxRating: Float? = null,
    @Serializable(with = DateAsStringSerializer::class)
    @QueryParam("from") val from: LocalDate? = null,
    @Serializable(with = DateAsStringSerializer::class)
    @QueryParam("to") val to: LocalDate? = null,
    @QueryParam("mapper") val mapper: String? = null,
    @BSMapFeatTag(MapFeatureTag.AI)
    @QueryParam("automapper") val automapper: Boolean? = null,
    @BSMapFeatTag(MapFeatureTag.Chroma)
    @QueryParam("chroma") val chroma: Boolean? = null,
    @BSMapFeatTag(MapFeatureTag.Noodle)
    @QueryParam("noodle") val noodle: Boolean? = null,
    @BSMapFeatTag(MapFeatureTag.MappingExtensions)
    @QueryParam("me") val me: Boolean? = null,
    @BSMapFeatTag(MapFeatureTag.Cinema)
    @QueryParam("cinema") val cinema: Boolean? = null,
    @BSMapFeatTag(MapFeatureTag.Ranked)
    @QueryParam("ranked") val ranked: Boolean? = null,
    @BSMapFeatTag(MapFeatureTag.Curated)
    @QueryParam("curated") val curated: Boolean? = null,
    @BSMapFeatTag(MapFeatureTag.VerifiedMapper)
    @QueryParam("verified") val verified: Boolean? = null,
    @BSMapFeatTag(MapFeatureTag.FullSpread)
    @QueryParam("fullSpread") val fullSpread: Boolean? = null,
    @QueryParam("sortOrder") val sortKey: String = "Relevance",
) {
    fun mapFeatureTagsMap(): Map<MapFeatureTag, Boolean> {
        return mapOf(
            MapFeatureTag.AI to automapper,
            MapFeatureTag.Chroma to chroma,
            MapFeatureTag.Noodle to noodle,
            MapFeatureTag.MappingExtensions to me,
            MapFeatureTag.Cinema to cinema,
            MapFeatureTag.Ranked to ranked,
            MapFeatureTag.Curated to curated,
            MapFeatureTag.VerifiedMapper to verified,
            MapFeatureTag.FullSpread to fullSpread,
        ).filterValues { it != null } as Map<MapFeatureTag, Boolean>
    }

    companion object {
        val default = MapFilterParam()
    }
}
