package io.ktlab.bshelper.service

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.ktlab.bshelper.model.BSHelperDatabase
import io.ktlab.bshelper.utils.Constants

actual class DBDriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(DesktopConfig.DB_URL)
        BSHelperDatabase.Schema.create(driver)
        return driver
    }
}