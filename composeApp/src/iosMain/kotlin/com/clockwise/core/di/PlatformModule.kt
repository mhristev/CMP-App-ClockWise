package com.clockwise.core.di

import com.clockwise.features.clockin.domain.service.LocationService
import com.clockwise.features.clockin.data.service.IOSLocationServiceImpl
import com.clockwise.features.sidemenu.platform.PlatformActions
import com.clockwise.features.sidemenu.platform.IOSPlatformActions
import com.liftric.kvault.KVault
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> { Darwin.create() }
    
    // Provide iOS-specific API configuration
    // Use IosApiConfig() for iOS Simulator, IosApiConfigPhysicalDevice() for physical device
    single<ApiConfig> { IosApiConfig() } // Change to IosApiConfigPhysicalDevice() for physical device
    
    // Provide KVault for iOS platform - iOS version doesn't need context
    single { 
        KVault("clockwise_secure_storage") 
    }
    
    // iOS location service with Core Location - use the proper domain service implementation
    single<LocationService> { 
        IOSLocationServiceImpl() 
    }
    
    // Platform-specific actions for side menu
    single<PlatformActions> {
        IOSPlatformActions()
    }
} 