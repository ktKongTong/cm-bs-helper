package io.ktlab.kown.database

import io.ktlab.kown.model.DownloadTaskBO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object NoOpsDBHelper : DBHelper {
    override suspend fun find(id: String): DownloadTaskBO? {
        return null
    }

    override suspend fun insert(task: DownloadTaskBO) {}
    override suspend fun batchInsert(tasks: List<DownloadTaskBO>) {}
    override suspend fun update(task: DownloadTaskBO) {}
    override suspend fun batchUpdate(tasks: List<DownloadTaskBO>) {
    }

    override suspend fun updateProgress(id: String, downloadedBytes: Long, lastModifiedAt: Long) {}

    override suspend fun remove(id: String) {}
    override fun getAllDownloadTaskFlow(): Flow<List<DownloadTaskBO>> = flow {}
    override suspend fun getAllDownloadTask(): List<DownloadTaskBO> { return listOf() }
    override suspend fun removeAll() {}
    override suspend fun removeByDays(days: Int) {}

}

