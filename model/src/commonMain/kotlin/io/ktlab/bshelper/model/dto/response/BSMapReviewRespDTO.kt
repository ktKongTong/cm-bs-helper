package io.ktlab.bshelper.model.dto.response

import io.ktlab.bshelper.model.dto.BSUserDTO
import io.ktlab.bshelper.model.dto.serializer.LocalDateTimeAsStringSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class BSMapReviewRespDTO(
    val docs: List<BSMapReviewDTO>
)
@Serializable
enum class BSMapReviewSentiment(val value: String) {
    POSITIVE("POSITIVE"),
    NEGATIVE("NEGATIVE")
}

@Serializable
data class BSMapReviewDTO(

    val creator: BSUserDTO,
    val id: Int,
    val sentiment: BSMapReviewSentiment,
    val text: String,
    @Serializable(with = LocalDateTimeAsStringSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeAsStringSerializer::class)
    val updatedAt: LocalDateTime,
)