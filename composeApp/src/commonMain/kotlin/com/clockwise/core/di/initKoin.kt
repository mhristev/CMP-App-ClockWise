package com.clockwise.core.di

import com.clockwise.features.auth.di.authModule
import com.clockwise.features.organization.di.organizationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

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
            organizationModule
        )
    }
}