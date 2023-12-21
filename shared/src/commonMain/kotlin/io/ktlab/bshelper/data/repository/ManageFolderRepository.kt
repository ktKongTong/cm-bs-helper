package io.ktlab.bshelper.data.repository

import io.ktlab.bshelper.model.BSHelperDatabase
import io.ktlab.bshelper.model.ManageFolder
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.SManageFolder
import io.ktlab.bshelper.model.enums.GameType
import io.ktlab.bshelper.model.toSManageFolder
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import okio.Path.Companion.toPath
import java.util.*

class ManageFolderRepository(
    private val userPreferenceRepository: UserPreferenceRepository,
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