package io.ktlab.bshelper.model.bsmg

import kotlinx.serialization.Serializable

@Serializable
data class BPList(
    val playlistTitle: String,
    val playlistAuthor: String,
    // base64 encoded image
    val image:String?= null,
    val playlistDescription: String? = null,
    val customData: CustomData = null,
    val songs: List<BPListSong>,
){


}

@Serializable
data class BPListSong(
    val key: String,
    val songName: String,
    val hash: String,
)