package io.ktlab.bshelper.di

import org.koin.core.module.Module
internal expect object PlatformModule {

    fun getModules(): List<Module>
}