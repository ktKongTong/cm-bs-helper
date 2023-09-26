package io.ktlab.bshelper.model.dto

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class PlaylistStats (
    val avgScore: Double,
    val downVotes: Long,
    val mapperCount: Long,
    val maxNps: Double,
    val minNps: Double,
    val totalDuration: Duration,
    val totalMaps: Long,
    val upVotes: Long,
)