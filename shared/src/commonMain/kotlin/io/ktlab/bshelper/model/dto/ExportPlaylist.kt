package io.ktlab.bshelper.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExportPlaylist(
    @SerialName("pn") val playlistName: String,
//    @SerialName("pk") val playlistKey: String,
    @SerialName("map") val mapItems: List<MapItem>,
)

@Serializable
data class MapItem(
    @SerialName("id") val mapId: String,
)
