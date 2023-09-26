package io.ktlab.bshelper.service

import app.cash.sqldelight.db.SqlDriver

expect class DBDriverFactory {
    fun createDriver(): SqlDriver
}