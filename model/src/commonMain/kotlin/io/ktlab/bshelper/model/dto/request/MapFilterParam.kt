package io.ktlab.bshelper.model.dto.request

import io.ktlab.bshelper.model.annotation.QueryParam


data class MapFilterParam(
    @QueryParam("q") val queryKey: String = "",
    @QueryParam("tags") val tags: String? = null,
    @QueryParam("maxNps") val maxNps: Double? = null,
    @QueryParam("minNps") val minNps: Double? = null,
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
    @QueryParam("sortOrder") val sortKey: String? = "Relevance",
)