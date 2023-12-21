package io.ktlab.bshelper.model

import kotlinx.serialization.Serializable

@Serializable
data class ManageFolderBackup(
    val id: Long,
    val backupDir: String,
    val targetDir: String,
    val backupTime: Long,
    val manageFolder: SManageFolder,
)