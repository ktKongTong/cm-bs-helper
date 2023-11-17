package io.ktlab.kown.database

import app.cash.sqldelight.db.SqlDriver
import io.ktlab.kown.model.DownloadTaskBO
import kotlinx.coroutines.flow.Flow


//expect class KownDBHelperProvider {
//
//}

interface DBHelper {

    suspend fun find(id: String): DownloadTaskBO?

    suspend fun insert(task: DownloadTaskBO)
    suspend fun batchInsert(tasks: List<DownloadTaskBO>)
    suspend fun update(task: DownloadTaskBO)
    suspend fun batchUpdate(tasks: List<DownloadTaskBO>)
    suspend fun updateProgress(id: String, downloadedBytes: Long, lastModifiedAt: Long)

    suspend fun remove(id: String)

    suspend fun getAllDownloadTask(): List<DownloadTaskBO>
     fun getAllDownloadTaskFlow(): Flow<List<DownloadTaskBO>>
     suspend fun removeAll() {}
    suspend fun removeByDays(days:Int)

    companion object {
        fun create(sqlDriver: SqlDriver): DBHelper {
            return SqlDelightDBHelper(sqlDriver)
        }
    }
}
