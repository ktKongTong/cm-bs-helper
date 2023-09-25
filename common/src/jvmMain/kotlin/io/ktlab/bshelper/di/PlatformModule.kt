package io.ktlab.bshelper.di

import io.ktlab.bshelper.service.DBAdapter
import io.ktlab.bshelper.service.DBDriverFactory
import io.ktlab.bshelper.service.createDataStore
import org.koin.dsl.module
import org.koin.core.module.Module

internal actual object PlatformModule {
     actual fun getModules(): List<Module> {
        val modules = module {
            single { DBAdapter.createDatabase(DBDriverFactory()) }
            single { createDataStore() }
        }
        return listOf(modules)
    }
}