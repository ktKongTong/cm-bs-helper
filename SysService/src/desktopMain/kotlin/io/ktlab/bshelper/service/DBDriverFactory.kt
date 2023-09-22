package io.ktlab.bshelper.service

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.ktlab.bshelper.model.BSHelperDatabase

actual class DBDriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        BSHelperDatabase.Schema.create(driver)
        return driver
    }
}