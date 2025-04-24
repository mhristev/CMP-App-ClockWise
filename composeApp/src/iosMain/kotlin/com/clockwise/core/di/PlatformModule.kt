package com.clockwise.core.di

import com.liftric.kvault.KVault
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> { Darwin.create() }
    
    // Provide iOS-specific API configuration
    single<ApiConfig> { IosApiConfig() }
    
    // Provide KVault for iOS platform
    single { 
        KVault("clockwise_secure_storage") 
    }
} 