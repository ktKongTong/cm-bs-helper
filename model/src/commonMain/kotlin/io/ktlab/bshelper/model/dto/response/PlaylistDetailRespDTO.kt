package io.ktlab.bshelper.model.dto.response

import io.ktlab.bshelper.model.dto.BSMapDTO
import io.ktlab.bshelper.model.dto.BSPlaylistFullDTO
import kotlinx.serialization.Serializable

@Serializable
data class BSPlaylistDetailRespDTO(
    val maps: List<BSMapDTO>,
    val playlist: BSPlaylistFullDTO,
)