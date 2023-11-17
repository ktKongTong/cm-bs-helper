package io.ktlab.bshelper.model.dto.response

import io.ktlab.bshelper.model.dto.BSMapDTO
import io.ktlab.bshelper.model.dto.BSPlaylistFullDTO
import kotlinx.serialization.Serializable


@Serializable
data class BSRespDTO(
    val docs: List<BSMapDTO>
)

@Serializable
data class BSPlaylistRespDTO(
    val docs: List<BSPlaylistFullDTO>
)