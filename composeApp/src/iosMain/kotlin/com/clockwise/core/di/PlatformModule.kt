package com.clockwise.core.di

import com.clockwise.features.location.data.platform.IOSLocationService
import com.clockwise.features.location.data.platform.PlatformLocationService
import com.clockwise.features.clockin.presentation.LocationService
import com.liftric.kvault.KVault
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> { Darwin.create() }
    
    // Provide iOS-specific API configuration
    single<ApiConfig> { IosApiConfig() }
    
    // Provide KVault for iOS platform - iOS version doesn't need context
    single { 
        KVault("clockwise_secure_storage") 
    }
    
    // iOS location service with Core Location
    single<PlatformLocationService> { 
        IOSLocationService() 
    }
    
    // Clock-in location service
    single<LocationService> { 
        LocationService() 
    }
} 