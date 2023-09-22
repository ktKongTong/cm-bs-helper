package io.ktlab.bshelper.model

data class MapFilterParam(
    val queryKey: String = "",
    val tags: String? = null,
    val maxNps: Double? = null,
    val minNps: Double? = null,
    val from: String? = null,
    val to: String? = null,
    val mapper: String? = null,
    val automapper: Boolean? = null,
    val chroma: Boolean? = null,
    val noodle: Boolean? = null,
    val me: Boolean? = null,
    val cinema: Boolean? = null,
    val ranked: Boolean? = null,
    val curated: Boolean? = null,
    val verified: Boolean? = null,
    val fullSpread: Boolean? = null,
    val sortKey: String? = "Relevance",
)