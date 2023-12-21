package io.ktlab.bshelper.data.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.model.BSHelperDatabase
import io.ktlab.bshelper.model.ManageFolder
import io.ktlab.bshelper.model.ManageFolderBackup
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.SManageFolder
import io.ktlab.bshelper.model.enums.GameType
import io.ktlab.bshelper.model.toSManageFolder
import io.ktlab.bshelper.platform.StorageService
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import okio.sink
import okio.source
import java.util.*

private val logger = KotlinLogging.logger {}

class ManageFolderRepository(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val storageService: StorageService,
    private val bsHelperDAO: BSHelperDatabase,
    private val downloaderRepository: DownloaderRepository,
) {
    fun getAllManageFolder(): List<SManageFolder> {
        return bsHelperDAO.manageFolderQueries.selectAll().executeAsList().map {
            it.toSManageFolder()
        }
    }
    fun createManageDir(manageDir: String,gameType: GameType): Result<SManageFolder> {
        bsHelperDAO.manageFolderQueries.selectByPath(manageDir).executeAsOneOrNull()?.let {
            if (it.active) {
                return Result.Error(Exception("manage dir has been added"))
            }
        }
        val id = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val manageDirPath = manageDir.toPath()
        val dbo = ManageFolder(
            id = id,
            name = manageDirPath.name,
            path = manageDirPath.toString(),
            gameType = gameType,
            active = false,
            createdAt = now,
            updatedAt = now,
        )
        try {
            bsHelperDAO.manageFolderQueries.insertAnyWay(dbo)
        }catch (e:Exception){
            return Result.Error(e)
        }
        return Result.Success(dbo.toSManageFolder())
    }
    private val json = Json { ignoreUnknownKeys = true }
    suspend fun backUpManageFolder(manageFolder: SManageFolder) {
        logger.debug { "backUpManageFolder $manageFolder" }
        val backupDir = storageService.getBackupDir()
        // check
        FileSystem.SYSTEM.exists(backupDir.resolve("backup.json")).let {
            if (!it) {
                logger.debug { "create backup.json" }
                backupDir.resolve("backup.json").toFile().createNewFile()
                backupDir.resolve("backup.json").toFile().sink().buffer().use {
                    it.writeUtf8("[]")
                }
            }
        }
        logger.debug { "read backup.json" }
        val backupJson = backupDir.resolve("backup.json")
            .toFile().source().buffer().use {
                it.readUtf8()
            }
        logger.debug { "disable manageFolder" }
        updateActiveManageFolderById(false,manageFolder.id)
        val backups = json.decodeFromString<Array<ManageFolderBackup>>(backupJson).toList()
        val backupFolderName = "backup_${manageFolder.name}_${manageFolder.id}_${manageFolder.gameType}_${Clock.System.now().toEpochMilliseconds()}"

        // todo: for different storage partition, need to improve
        val backupFolder = storageService.getBackupDir(manageFolder.path.toPath())

        FileSystem.SYSTEM.createDirectory(backupFolder.resolve(backupFolderName),mustCreate = true)
        logger.debug { "create manageFolderbackup" }
        val backupTime = Clock.System.now().toEpochMilliseconds()
        val backup = ManageFolderBackup(
            id = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE,
            backupDir = backupFolder.resolve(backupFolderName).toString(),
            targetDir = manageFolder.path,
            backupTime = backupTime,
            manageFolder = manageFolder,
        )
        logger.debug { "manage folder move" }
        FileSystem.SYSTEM.list(manageFolder.path.toPath()).forEach {
            FileSystem.SYSTEM.atomicMove(it,backupFolder.resolve(backupFolderName).resolve(it.name))
        }
        logger.debug { "manage folder move over" }
        backupDir.resolve("backup.json").toFile().sink().buffer().use {
            it.writeUtf8(json.encodeToString((backups + backup).toTypedArray()))
        }
        logger.debug { "manage folder backup write over" }
    }

    suspend fun restoreFromBackup(backup: ManageFolderBackup) {
        val backupDir = storageService.getBackupDir()
        val backupJson = backupDir.resolve("backup.json")
            .toFile().source().buffer().use {
                it.readUtf8()
            }
        val backups = json.decodeFromString<Array<ManageFolderBackup>>(backupJson).toList()
        val backupFolder = backup.backupDir.toPath()
        if (!FileSystem.SYSTEM.exists(backup.targetDir.toPath())) {
            FileSystem.SYSTEM.createDirectory(backup.targetDir.toPath(),mustCreate = true)
        }
        FileSystem.SYSTEM.list(backupFolder).forEach {
            FileSystem.SYSTEM.atomicMove(it,backup.targetDir.toPath().resolve(it.name))
        }
        backupDir.resolve("backup.json").toFile().sink().buffer().use {
            it.writeUtf8(json.encodeToString(backups.filter { it.id != backup.id }.toTypedArray()))
        }
//        remove backup folder
        FileSystem.SYSTEM.delete(backupFolder)

        updateActiveManageFolderById(true,backup.manageFolder.id)
    }

    suspend fun getAllBackup(): List<ManageFolderBackup> {
        val backupDir = storageService.getBackupDir()
        FileSystem.SYSTEM.exists(backupDir.resolve("backup.json")).let {
            if (!it) {
                backupDir.resolve("backup.json").toFile().createNewFile()
                backupDir.resolve("backup.json").toFile().sink().buffer().use {
                    it.writeUtf8("[]")
                }
            }
        }
        val backupJson = backupDir.resolve("backup.json")
            .toFile().source().buffer().use {
                it.readUtf8()
            }
        return json.decodeFromString<Array<ManageFolderBackup>>(backupJson).toList()
    }

    fun updateActiveManageFolderById(active:Boolean,id:Long){
        bsHelperDAO.manageFolderQueries.updateActiveById(active,id)
    }

    suspend fun deleteManageFolder(manageFolder:SManageFolder) {
        bsHelperDAO.transaction {
            downloaderRepository.removeAllByManageFolderId(manageFolder.id)
            bsHelperDAO.manageFolderQueries.deleteById(manageFolder.id)
            bsHelperDAO.fSMapQueries.deleteAllFSMapByManageFolderId(manageFolder.id)
            bsHelperDAO.fSPlaylistQueries.deleteByManageFolderId(manageFolder.id)
        }
    }

    fun clearAllData() {
        bsHelperDAO.transaction {
            downloaderRepository.removeAllMatch { true }
            bsHelperDAO.manageFolderQueries.deleteAll()
            bsHelperDAO.fSMapQueries.deleteAllFSMap()
            bsHelperDAO.fSPlaylistQueries.deleteAll()
            bsHelperDAO.bSMapVersionQueries.deleteAll()
            bsHelperDAO.mapDifficultyQueries.deleteAll()
            bsHelperDAO.bSMapQueries.deleteAll()
            bsHelperDAO.bSUserQueries.deleteAll()
            bsHelperDAO.bSPlaylistQueries.deleteAll()
        }
    }
}