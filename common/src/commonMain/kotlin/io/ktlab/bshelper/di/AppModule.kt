package io.ktlab.bshelper.di

import io.ktlab.bshelper.api.BeatSaverAPI
import io.ktlab.bshelper.api.ToolAPI
import io.ktlab.bshelper.repository.FSMapRepository
import io.ktlab.bshelper.repository.PlaylistRepository
import io.ktlab.bshelper.repository.UserPreferenceRepository
import io.ktlab.bshelper.service.HttpClientModuleProviderBase
import io.ktlab.bshelper.viewmodel.BeatSaverViewModel
import io.ktlab.bshelper.viewmodel.HomeViewModel
import io.ktlab.bshelper.viewmodel.ToolboxViewModel
import org.koin.dsl.module

object AppModule {
    private val sysServiceModule = module {
        single {
            HttpClientModuleProviderBase().configureClient()
        }
        single {
            BeatSaverAPI(get())
        }
        single {
            ToolAPI(get())
        }
        single {
            PlaylistRepository(get(),get(),get())
        }
        single {
            FSMapRepository(get(),get())
        }
        single {
            UserPreferenceRepository(get())
        }
    }

    private val viewModelModule = module {
        single<HomeViewModel> { HomeViewModel(get(),get(),get()) }
        single<ToolboxViewModel> { ToolboxViewModel(get(),get()) }
        single<BeatSaverViewModel> { BeatSaverViewModel(get(),get(),get()) }
    }

    /**
     * Provider all modules includes platform specific modules
     */
    fun getModules() = PlatformModule.getModules()+ listOf(sysServiceModule, viewModelModule)
}

