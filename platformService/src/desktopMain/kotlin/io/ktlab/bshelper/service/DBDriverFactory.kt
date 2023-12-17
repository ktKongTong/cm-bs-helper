package io.ktlab.bshelper.service

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.ktlab.bshelper.model.BSHelperDatabase

actual class DBDriverFactory(
    storageService: StorageService
) {

    private val configPath = storageService.getConfigDir()
    private val DB_URL: String = "jdbc:sqlite:$configPath/bs-helper.db"
    actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(DB_URL)
        BSHelperDatabase.Schema.create(driver)
        return driver
    }

}