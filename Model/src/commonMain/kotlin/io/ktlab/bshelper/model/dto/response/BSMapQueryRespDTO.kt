package io.ktlab.bshelper.model.dto.response

import io.ktlab.bshelper.model.dto.BSMapDTO
import kotlinx.serialization.Serializable


@Serializable
data class BSRespDTO(
    val docs: List<BSMapDTO>
)