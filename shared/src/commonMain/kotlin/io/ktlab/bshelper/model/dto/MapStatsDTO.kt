package io.ktlab.bshelper.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class MapStatsDTO(
    val plays: Long,
    val downloads: Long,
    val upvotes: Long,
    val downvotes: Long,
    val score: Double,
) {
//    fun convertToEntity(): MapStats {
//        return MapStats(
//            plays = plays,
//            downloads = downloads,
//            upvotes = upvotes,
//            downvotes = downvotes,
//            score = score,
//        )
//    }
}
