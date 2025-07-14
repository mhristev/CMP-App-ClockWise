package com.clockwise.core.di

import com.clockwise.features.location.platform.IOSLocationService
import com.clockwise.features.location.platform.PlatformLocationService
import com.clockwise.features.clockin.presentation.LocationService
import com.liftric.kvault.KVault
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { Darwin.create() }
        
        // Provide Api configuration
        single<ApiConfig> { IosApiConfig() }
        
        // Provide KVault initialization for iOS platform
        single {
            KVault("clockwise_secure_storage")
        }
        
        // iOS location service
        single<PlatformLocationService> { 
            IOSLocationService() 
        }
        
        // Clock-in location service
        single<LocationService> { 
            LocationService() 
        }
    }
