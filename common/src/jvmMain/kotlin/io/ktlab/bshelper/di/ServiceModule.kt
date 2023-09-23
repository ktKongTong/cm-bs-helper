package io.ktlab.bshelper.di

import io.ktlab.bshelper.service.DBAdapter
import io.ktlab.bshelper.service.DBDriverFactory
import io.ktlab.bshelper.service.createDataStore
import io.ktlab.bshelper.service.getDataStore
import org.koin.dsl.module
import org.koin.core.module.Module

actual object ServiceModule {
     fun getModules(): List<Module> {
        val modules = module {
            single { DBAdapter.createDatabase(DBDriverFactory()) }
            single { createDataStore() }
        } + AppModule.module
        return modules
    }
}