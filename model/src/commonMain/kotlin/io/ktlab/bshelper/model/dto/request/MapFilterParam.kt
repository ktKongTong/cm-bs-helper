package io.ktlab.bshelper.model.dto.request

import io.ktlab.bshelper.model.annotation.QueryParam
import io.ktlab.bshelper.model.enums.MapFeatureTag


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
    @QueryParam("from") val from: String? = null,
    @QueryParam("to") val to: String? = null,
    @QueryParam("tags") val mapper: String? = null,
    @QueryParam("automapper") val automapper: Boolean? = null,
    @QueryParam("chroma") val chroma: Boolean? = null,
    @QueryParam("noodle") val noodle: Boolean? = null,
    @QueryParam("me") val me: Boolean? = null,
    @QueryParam("cinema") val cinema: Boolean? = null,
    @QueryParam("ranked") val ranked: Boolean? = null,
    @QueryParam("curated") val curated: Boolean? = null,
    @QueryParam("verified") val verified: Boolean? = null,
    @QueryParam("fullSpread") val fullSpread: Boolean? = null,
    @QueryParam("sortOrder") val sortKey: String = "Relevance",
) {
    fun mapFeatureTagsMap() : Map<MapFeatureTag,Boolean> {
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
}