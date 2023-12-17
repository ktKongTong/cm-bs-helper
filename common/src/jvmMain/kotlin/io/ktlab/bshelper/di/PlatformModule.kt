package io.ktlab.bshelper.di

import io.ktlab.bshelper.service.*
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual object PlatformModule {
     actual fun getModules(): List<Module> {
        val modules = module {
            single { createMediaPlayer() }
            single { StorageService() }
            single { createDataStore(get()) }
            single { DBAdapter.createDatabase(DBDriverFactory(get())) }
            single { createClipboardManager(BSHelperClipboardFactory()) }

        }
        return listOf(modules)
    }
}