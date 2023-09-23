package io.ktlab.bshelper.di

import android.content.Context
import io.ktlab.bshelper.service.AndroidMediaPlayer
import io.ktlab.bshelper.service.DBAdapter
import io.ktlab.bshelper.service.DBDriverFactory
import io.ktlab.bshelper.service.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

actual object ServiceModule {

    fun getModules(context: Context): List<Module> {
        val modules = module {
            single { AndroidMediaPlayer() }
            single { DBAdapter.createDatabase(DBDriverFactory(context)) }
            single { createDataStore(context) }
        } + AppModule.module
        return modules
    }
}