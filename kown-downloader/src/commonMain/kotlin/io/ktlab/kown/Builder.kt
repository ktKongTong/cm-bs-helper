package io.ktlab.kown

import app.cash.sqldelight.db.SqlDriver
import io.ktlab.kown.database.DBHelper

class KownloaderBuilder {
    private var kownConfig = KownConfig()
    fun setDatabaseEnabled(enabled: Boolean) = apply {
        kownConfig.databaseEnabled = enabled
    }
    fun setDataBaseDriver(driver: SqlDriver) = apply {
        kownConfig.dbHelper = DBHelper.create(driver)
    }

    fun setRetryCount(count: Int = 0) = apply {
        kownConfig.retryEnabled = count > 0
        kownConfig.retryCount = count
    }

    fun setChunkSize(size: Int) = apply {
        kownConfig.chunkSize = size
    }

    fun setMaxConcurrentDownloads(count: Int) = apply {
        kownConfig.concurrentDownloads = count
    }

    fun build(): Kownloader {
        return Kownloader(kownConfig)
    }
}