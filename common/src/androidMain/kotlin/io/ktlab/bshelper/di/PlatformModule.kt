package io.ktlab.bshelper.di

import io.ktlab.bshelper.service.*
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual object PlatformModule {

    actual fun getModules(): List<Module> {
        val modules = module {
            single { AndroidMediaPlayer() }
            single { DBAdapter.createDatabase(DBDriverFactory(get())) }
            single { StorageService(get()) }
            single { createDataStore(get()) }
        }
        return listOf(modules)
    }
}