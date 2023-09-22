package io.ktlab.bshelper.service

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.ktlab.bshelper.model.BSHelperDatabase

actual class DBDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(BSHelperDatabase.Schema, context, "bshelper.db")
    }
}