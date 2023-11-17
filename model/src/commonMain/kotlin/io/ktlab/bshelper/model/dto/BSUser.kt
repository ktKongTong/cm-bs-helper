package io.ktlab.bshelper.model.dto

//import io.ktkt.bshelper.data.model.room.BSUser
import io.ktlab.bshelper.model.BSUser
import kotlinx.serialization.Serializable

@Serializable
data class BSUserDTO(
    val id: Int,
    val name: String,
    val avatar: String,
    val hash: String? = null,
    val description: String = "",
    val type: String,
    val admin: Boolean,
    val curator: Boolean,
    val playlistUrl: String,
    val verifiedMapper: Boolean? = null,
){
    fun convertToEntity(): BSUser {
        return BSUser(
            id = id,
            name = name,
            avatar = avatar,
//            hash = hash,
            description = description,
            type = type,
            admin = admin,
            curator = curator,
            playlistUrl = playlistUrl,
            verifiedMapper = verifiedMapper
        )
    }
}
