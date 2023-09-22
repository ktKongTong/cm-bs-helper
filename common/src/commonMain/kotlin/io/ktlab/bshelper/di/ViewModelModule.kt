package io.ktlab.bshelper.di

import io.ktlab.bshelper.viewmodel.HomeViewModel
import io.ktlab.bshelper.viewmodel.ToolboxViewModel
import org.koin.dsl.module

object ViewModelModule  {
    val viewModelModule = module {
//        single<HomeViewModel> { HomeViewModel() }
//        single<ToolboxViewModel> { ToolboxViewModel() }
    }

}