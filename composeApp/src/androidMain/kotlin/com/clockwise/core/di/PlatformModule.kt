package com.clockwise.core.di

import com.clockwise.features.clockin.data.service.AndroidLocationServiceImpl
import com.clockwise.features.clockin.domain.service.LocationService
import org.koin.dsl.module

actual val platformModule = module {
    // Override the MockLocationService with the real AndroidLocationServiceImpl
    single<LocationService>(override = true) { 
        AndroidLocationServiceImpl(get()) 
    }
}