package io.ktlab.bshelper.model

import io.ktlab.bshelper.model.enums.GameType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class SManageFolder(
    val id: Long,
    val name: String,
    val path: String,
    val gameType: GameType,
    val active: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
){
    companion object {
        fun fromManageFolder(manageFolder: ManageFolder): SManageFolder {
            return SManageFolder(
                id = manageFolder.id,
                name = manageFolder.name,
                path = manageFolder.path,
                gameType = manageFolder.gameType,
                active = manageFolder.active,
                createdAt = manageFolder.createdAt,
                updatedAt = manageFolder.updatedAt,
            )
        }
    }
}

fun ManageFolder.toSManageFolder(): SManageFolder {
    return SManageFolder.fromManageFolder(this)
}