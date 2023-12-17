package io.ktlab.bshelper.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistStats(
    val avgScore: Double,
    val downVotes: Long,
    val mapperCount: Long,
    val maxNps: Double,
    val minNps: Double,
    val totalDuration: Long,
    val totalMaps: Long,
    val upVotes: Long,
)
