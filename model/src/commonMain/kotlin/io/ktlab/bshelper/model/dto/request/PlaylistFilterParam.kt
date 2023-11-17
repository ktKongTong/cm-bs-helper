package io.ktlab.bshelper.model.dto.request

import io.ktlab.bshelper.model.annotation.QueryParam

data class PlaylistFilterParam(
    @QueryParam("q") val queryKey: String = "",
    @QueryParam("maxNps") val maxNps: Double? = null,
    @QueryParam("minNps") val minNps: Double? = null,
    @QueryParam("from") val from: String? = null,
    @QueryParam("to") val to: String? = null,
    @QueryParam("curated") val curated: Boolean? = null,
    @QueryParam("verified") val verified: Boolean? = null,
    @QueryParam("sortOrder") val sortKey: String = "Relevance",
)