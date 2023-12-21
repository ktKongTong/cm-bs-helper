package io.ktlab.bshelper.di

import io.ktlab.bshelper.data.RuntimeEventFlow
import io.ktlab.bshelper.data.api.BeatSaverAPI
import io.ktlab.bshelper.data.api.ToolAPI
import io.ktlab.bshelper.data.repository.BSAPIRepository
import io.ktlab.bshelper.data.repository.DownloaderRepository
import io.ktlab.bshelper.data.repository.FSMapRepository
import io.ktlab.bshelper.data.repository.ManageFolderRepository
import io.ktlab.bshelper.data.repository.PlaylistRepository
import io.ktlab.bshelper.data.repository.UserPreferenceRepository
import io.ktlab.bshelper.platform.HttpClientModuleProviderBase
import io.ktlab.bshelper.ui.viewmodel.BeatSaverViewModel
import io.ktlab.bshelper.ui.viewmodel.GlobalViewModel
import io.ktlab.bshelper.ui.viewmodel.HomeViewModel
import io.ktlab.bshelper.ui.viewmodel.ToolboxViewModel
import org.koin.dsl.module

object AppModule {
    private val sysServiceModule =
        module {
            single { RuntimeEventFlow() }
            factory {
                HttpClientModuleProviderBase().configureClient()
            }
            single {
                BeatSaverAPI(get())
            }
            single {
                ToolAPI(HttpClientModuleProviderBase().configureToolAPIClient())
            }
            single {
                BSAPIRepository(get())
            }
            single {
                PlaylistRepository(get(), get(),get(), get(), get())
            }
            single {
                FSMapRepository(get(),get(), get())
            }
            single { ManageFolderRepository(get(), get(), get(),get()) }

            single {
                UserPreferenceRepository(get())
            }
            single {
                DownloaderRepository(get(), get(), get(), get(), get(), get())
            }
        }

    private val viewModelModule =
        module {
            single<GlobalViewModel> { GlobalViewModel(get(), get(), get(), get(), get(),get(),get(),get()) }
            single<HomeViewModel> { HomeViewModel( get(), get(), get(), get()) }
            single<ToolboxViewModel> { ToolboxViewModel( get(), get(), get(), get()) }
            single<BeatSaverViewModel> { BeatSaverViewModel(get(), get(), get(), get()) }
        }

    /**
     * Provider all modules includes platform specific modules
     */
    fun getModules() = PlatformModule.getModules() + listOf(sysServiceModule, viewModelModule)
}
