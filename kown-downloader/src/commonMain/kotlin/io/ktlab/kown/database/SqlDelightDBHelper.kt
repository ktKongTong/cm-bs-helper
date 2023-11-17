package io.ktlab.kown.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import io.ktlab.kown.model.TaskStatus
import io.ktlab.kown.model.asString
import io.ktlab.kown.model.*
import io.ktlab.kown.model.stringAsTaskStatusMapper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json



private fun KownDownloadTaskModel.convertToDownloadTaskBO():DownloadTaskBO {
    return DownloadTaskBO(
        taskId = taskId,
        title = title,
        url = url,
        eTag = eTag,
        tag = tag,
        headers = headers,
        dirPath = dirPath,
        filename = filename,
        status = stringAsTaskStatusMapper(status),
        totalBytes = totalBytes,
        downloadedBytes = downloadedBytes,
        lastModifiedAt = lastModifiedAt,
        renameAble = renameAble,
        createdAt = createAt,
        renameStrategy = RenameStrategy.valueOf(renameStrategy?:"DEFAULT"),
        relateEntityId = relateEntityId,
        downloadListener = null
    )
}

class SqlDelightDBHelper(private val driver: SqlDriver):DBHelper {


    private var kownDatabase: KownDatabase

    private val stringOfStringMapAdapter = object : ColumnAdapter<Map<String,String>,String> {
        override fun decode(databaseValue: String): Map<String, String> {
            return Json.decodeFromString(MapSerializer(String.serializer(), String.serializer()), databaseValue)
        }

        override fun encode(value: Map<String, String>): String {
            return Json.encodeToJsonElement(MapSerializer(String.serializer(), String.serializer()), value).toString()
        }
    }
    init {
        KownDatabase.Schema.create(driver)
        kownDatabase = KownDatabase(driver = driver, KownDownloadTaskModelAdapter = KownDownloadTaskModel.Adapter(headersAdapter = stringOfStringMapAdapter))
        runBlocking {
            syncOnStart()
        }
    }

    private suspend fun syncOnStart() {
        getAllDownloadTask().forEach {
            when (it.status) {
                TaskStatus.Running, is TaskStatus.Queued, TaskStatus.PostProcessing -> {
                    it.status = TaskStatus.Paused(it.status)
                    update(it)
                }
            }
        }
    }

    private fun initTable() {
        driver.execute(null, """
          CREATE TABLE IF NOT EXISTS KownDownloadTaskModel (
              taskId TEXT NOT NULL PRIMARY KEY,
              title TEXT NOT NULL,
              url TEXT NOT NULL,
              eTag TEXT NOT NULL,
              tag TEXT NOT NULL,
              headers TEXT NULL,
              dirPath TEXT NOT NULL,
              filename TEXT NOT NULL,
              status TEXT NOT NULL,
              totalBytes INTEGER NOT NULL,
              downloadedBytes INTEGER NOT NULL,
              lastModifiedAt INTEGER NOT NULL,
              renameAble INTEGER NOT NULL,
              renameStrategy TEXT NOT NULL,
              relateEntityId TEXT
          )
          """.trimMargin(), 0)
    }

    override suspend fun find(id: String): DownloadTaskBO? {
//        kownDatabase.kownModelQueries.insert()
        return kownDatabase.kownModelQueries.selectByTaskId(id).executeAsOneOrNull()?.convertToDownloadTaskBO()
//        val model = Query(1_647_052_218, arrayOf("KownDownloadTaskModel"), driver, "SELECT * FROM `KownDownloadTaskModel` WHERE taskId = ?1") {
//            sqlCursor: SqlCursor -> DownloadTaskModel(
//                taskId = sqlCursor.getString(0)!!,
//                title = sqlCursor.getString(1)!!,
//                url = sqlCursor.getString(2)!!,
//                eTag = sqlCursor.getString(3)!!,
//                tag = sqlCursor.getString(4)!!,
//                headers = sqlCursor.getString(5)?.let { json.decodeFromString(MapSerializer(String.serializer(), String.serializer()), it) } ?: emptyMap(),
//                dirPath = sqlCursor.getString(6)!!,
//                filename = sqlCursor.getString(7)!!,
//                status = stringAsTaskStatusMapper(sqlCursor.getString(8)!!),
//                totalBytes = sqlCursor.getLong(9)!!,
//                downloadedBytes = sqlCursor.getLong(10)!!,
//                lastModifiedAt = sqlCursor.getLong(11)!!,
//                renameAble = sqlCursor.getBoolean(12)!!,
//                renameStrategy = sqlCursor.getString(13)?.let { RenameStrategy.valueOf(it) } ?: RenameStrategy.DEFAULT,
//                relateEntityId = sqlCursor.getString(14),
//            )
//        }.executeAsOneOrNull()
//        return model?.toDownloadTaskBO()
    }

    override suspend fun insert(task: DownloadTaskBO) {
        kownDatabase.kownModelQueries.insert(
            KownDownloadTaskModel(
                taskId = task.taskId,
                title = task.title,
                url = task.url,
                eTag = task.eTag,
                tag = task.tag,
                headers = task.headers,
                dirPath = task.dirPath,
                filename = task.filename,
                status = task.status.asString(),
                totalBytes = task.totalBytes,
                downloadedBytes = task.downloadedBytes,
                lastModifiedAt = task.lastModifiedAt,
                createAt = task.createdAt,
                renameAble = task.renameAble,
                renameStrategy = task.renameStrategy.toString(),
                relateEntityId = task.relateEntityId
            )
        )
//        driver.execute(null, """
//          INSERT OR REPLACE INTO KownDownloadTaskModel (
//              taskId,
//              title,
//              url,
//              eTag,
//              tag,
//              headers,
//              dirPath,
//              filename,
//              status,
//              totalBytes,
//              downloadedBytes,
//              lastModifiedAt,
//              renameAble,
//              renameStrategy,
//              relateEntityId
//          ) VALUES (?1,?2,?3,?4,?5,?6,?7,?8,?9,?10,?11,?12,?13,?14,?15)
//          """.trimMargin(), 15) {
//            bindString(0, task.taskId)
//            bindString(1, task.title)
//            bindString(2, task.url)
//            bindString(3, task.eTag)
//            bindString(4, task.tag)
//            bindString(5, task.headers.toString())
//            bindString(6, task.dirPath)
//            bindString(7, task.filename)
//            bindString(8, task.status.toString())
//            bindLong(9, task.totalBytes)
//            bindLong(10, task.downloadedBytes)
//            bindLong(11, task.lastModifiedAt)
//            bindBoolean(12, task.renameAble)
//            bindString(13, task.renameStrategy.toString())
//            bindString(14, task.relateEntityId)
//        }
    }

    override suspend fun batchInsert(tasks: List<DownloadTaskBO>) {
        kownDatabase.transaction {
            tasks.forEach{task ->
                kownDatabase.kownModelQueries.insert(
                    KownDownloadTaskModel(
                        taskId = task.taskId,
                        title = task.title,
                        url = task.url,
                        eTag = task.eTag,
                        tag = task.tag,
                        headers = task.headers,
                        dirPath = task.dirPath,
                        filename = task.filename,
                        status = task.status.asString(),
                        totalBytes = task.totalBytes,
                        downloadedBytes = task.downloadedBytes,
                        lastModifiedAt = task.lastModifiedAt,
                        createAt    = task.createdAt,
                        renameAble = task.renameAble,
                        renameStrategy = task.renameStrategy.toString(),
                        relateEntityId = task.relateEntityId
                    )
                )
            }
        }
    }
    override suspend fun update(task: DownloadTaskBO) {
        insert(task)
    }
    override suspend fun batchUpdate(tasks: List<DownloadTaskBO>) {
        kownDatabase.transaction {
            tasks.forEach{task ->
                kownDatabase.kownModelQueries.insert(
                    KownDownloadTaskModel(
                        taskId = task.taskId,
                        title = task.title,
                        url = task.url,
                        eTag = task.eTag,
                        tag = task.tag,
                        headers = task.headers,
                        dirPath = task.dirPath,
                        filename = task.filename,
                        status = task.status.asString(),
                        totalBytes = task.totalBytes,
                        downloadedBytes = task.downloadedBytes,
                        lastModifiedAt = task.lastModifiedAt,
                        createAt    = task.createdAt,
                        renameAble = task.renameAble,
                        renameStrategy = task.renameStrategy.toString(),
                        relateEntityId = task.relateEntityId
                    )
                )
            }
        }
    }

    override suspend fun updateProgress(id: String, downloadedBytes: Long, lastModifiedAt: Long) {

//        driver.execute(null, """
//            |UPDATE KownDownloadTaskModel SET downloadedBytes = ?1, lastModifiedAt = ?2 WHERE taskId = ?3
//          """.trimMargin(), 3) {
//            bindLong(1, downloadedBytes)
//            bindLong(2, lastModifiedAt)
//            bindString(3, id)
//        }
    }

    override suspend fun remove(id: String) {
        driver.execute(null, """
          |DELETE FROM KownDownloadTaskModel WHERE taskId = ?1
          """.trimMargin(), 1) {
            bindString(1, id)
        }
    }
    override suspend fun getAllDownloadTask(): List<DownloadTaskBO> = kownDatabase.kownModelQueries.selectAll().executeAsList().map { it.convertToDownloadTaskBO() }
    @OptIn(FlowPreview::class)
    override fun getAllDownloadTaskFlow(): Flow<List<DownloadTaskBO>> = kownDatabase.kownModelQueries.selectAll()
        .asFlow()
        .debounce(1000)
        .mapToList(Dispatchers.IO)
        .map { it.map { it.convertToDownloadTaskBO() } }

    override suspend fun removeByDays(days: Int) {
        TODO()
    }
    override suspend fun removeAll() {
        kownDatabase.transaction {
            kownDatabase.kownModelQueries.deleteAll()
        }
    }
}