package io.ktlab.bshelper.di

import io.ktlab.bshelper.api.BeatSaverAPI
import io.ktlab.bshelper.api.ToolAPI
import io.ktlab.bshelper.repository.*
import io.ktlab.bshelper.service.HttpClientModuleProviderBase
import io.ktlab.bshelper.viewmodel.BeatSaverViewModel
import io.ktlab.bshelper.viewmodel.GlobalViewModel
import io.ktlab.bshelper.viewmodel.HomeViewModel
import io.ktlab.bshelper.viewmodel.ToolboxViewModel
import org.koin.dsl.module

object AppModule {
    private val sysServiceModule =
        module {
            single { RuntimeEventFlow() }
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
                PlaylistRepository(get(), get(), get(), get())
            }
            single {
                FSMapRepository(get(), get())
            }
            single {
                UserPreferenceRepository(get())
            }
            single {
                DownloaderRepository(get(), get(), get(), get(), get(), get())
            }
        }

    private val viewModelModule =
        module {
            single<GlobalViewModel> { GlobalViewModel(get(), get(), get(), get(), get()) }
            single<HomeViewModel> { HomeViewModel(get(), get(), get(), get(), get()) }
            single<ToolboxViewModel> { ToolboxViewModel(get(), get(), get(), get()) }
            single<BeatSaverViewModel> { BeatSaverViewModel(get(), get(), get(), get(), get()) }
//        factory<BeatSaverViewModel> {
//            (playlistId:String?) -> BeatSaverViewModel(get(),get(),get(),get(),get(),playlistId)
//        }
        }

    /**
     * Provider all modules includes platform specific modules
     */
    fun getModules() = PlatformModule.getModules() + listOf(sysServiceModule, viewModelModule)
}
