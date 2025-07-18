package com.clockwise.core.di

import com.clockwise.features.clockin.domain.service.LocationService
import com.clockwise.features.clockin.data.service.IOSLocationServiceImpl
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
    
    // iOS location service with Core Location - use the proper domain service implementation
    single<LocationService> { 
        IOSLocationServiceImpl() 
    }
} 