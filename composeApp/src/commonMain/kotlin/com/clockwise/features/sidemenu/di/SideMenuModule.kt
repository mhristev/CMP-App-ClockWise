package com.clockwise.features.sidemenu.di

import com.clockwise.features.sidemenu.presentation.SideMenuViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sideMenuModule = module {
    viewModelOf(::SideMenuViewModel)
}