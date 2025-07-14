package com.clockwise.core.di

import android.content.Context
import androidx.activity.ComponentActivity
import com.clockwise.features.clockin.presentation.LocationService
import com.liftric.kvault.KVault
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> { OkHttp.create() }
    
    // Provide Android-specific API configuration
    single<ApiConfig> { AndroidApiConfig() }
    
    // Provide KVault for Android platform
    single { 
        KVault(androidContext(), "clockwise_secure_storage") 
    }
    
    // Android location service
    single<LocationService> { 
        LocationService(
            context = androidContext(),
            activity = androidContext() as ComponentActivity
        ) 
    }
}
