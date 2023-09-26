package io.ktlab.bshelper.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class BSPlaylist (
    val createdAt: String,
    val description: String,
    val downloadURL: String,
    val name: String,
    val owner: BSUserDTO,
    val playlistId: Int,
    val playlistImage: String,
    val playlistImage512: String,
    val songsChangedAt: String,
    val stats: PlaylistStats,
    val type: String,
    val updatedAt: String,
)