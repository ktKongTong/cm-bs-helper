package io.ktlab.bshelper.di

import io.ktlab.bshelper.api.BeatSaverAPI
import io.ktlab.bshelper.api.ToolAPI
import io.ktlab.bshelper.repository.FSMapRepository
import io.ktlab.bshelper.repository.PlaylistRepository
import io.ktlab.bshelper.repository.UserPreferenceRepository
import io.ktlab.bshelper.service.HttpClientModuleProviderBase
import org.koin.dsl.module

object AppModule {
    val module = module {
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
}

