package io.ktlab.bshelper.platform

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.model.BSHelperDatabase

private val logger = KotlinLogging.logger {}

actual class DBDriverFactory(
    storageService: StorageService,
) {
    private val configPath = storageService.getConfigDir()
    private val dbURL: String = "jdbc:sqlite:$configPath/bs-helper.db"

    actual fun createDriver(): SqlDriver {
        logger.debug { "createDBDriver: $dbURL" }
        val driver: SqlDriver = JdbcSqliteDriver(dbURL)
        BSHelperDatabase.Schema.create(driver)
        return driver
    }
}
