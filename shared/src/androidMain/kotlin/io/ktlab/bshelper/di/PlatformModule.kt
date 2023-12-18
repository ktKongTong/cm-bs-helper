package io.ktlab.bshelper.di

import io.ktlab.bshelper.platform.DBAdapter
import io.ktlab.bshelper.platform.createClipboardManager
import io.ktlab.bshelper.platform.createDataStore
import io.ktlab.bshelper.platform.createMediaPlayer
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual object PlatformModule {
    actual fun getModules(): List<Module> {
        val modules =
            module {
                single { createClipboardManager(io.ktlab.bshelper.platform.BSHelperClipboardFactory(get())) }
                single { createMediaPlayer() }
                single { DBAdapter.createDatabase(io.ktlab.bshelper.platform.DBDriverFactory(get())) }
                single { io.ktlab.bshelper.platform.StorageService(get()) }
                single { createDataStore(get()) }
            }
        return listOf(modules)
    }
}
