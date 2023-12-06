package io.ktlab.bshelper.model.dto.response

import io.ktlab.bshelper.model.dto.BSMapDTO
import io.ktlab.bshelper.model.dto.BSPlaylistFullDTO
import kotlinx.serialization.Serializable

@Serializable
data class BSPlaylistDetailRespDTO(
    val maps: List<PlaylistDetailMapDTO>,
    val playlist: BSPlaylistFullDTO,
)
@Serializable
data class PlaylistDetailMapDTO(
    val map: BSMapDTO,
    val order: Double,
)