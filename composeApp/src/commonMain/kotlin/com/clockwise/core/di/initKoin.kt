package com.clockwise.core.di

import com.clockwise.features.auth.di.authModule
import com.clockwise.features.clockin.presentation.ClockInViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

// Simple inline clock in module 
val inlineClockInModule = module {
    viewModel { ClockInViewModel(get()) }
}

/**
 * Initializes Koin dependency injection
 */
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            sharedModule, 
            platformModule,
            authModule,
            inlineClockInModule
        )
    }
}