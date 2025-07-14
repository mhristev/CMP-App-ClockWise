package com.clockwise.core.di

import android.content.Context
import androidx.activity.ComponentActivity
import com.clockwise.features.location.platform.AndroidLocationService
import com.clockwise.features.location.platform.PlatformLocationService
import com.clockwise.features.clockin.presentation.LocationService
import com.liftric.kvault.KVault
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { OkHttp.create() }
        
        // Provide Api configuration
        single<ApiConfig> { AndroidApiConfig() }
        
        // Provide KVault initialization for Android platform
        single {
            val context = get<Context>()
            KVault(context, "clockwise_secure_storage")
        }
        
        // Android location service
        single<PlatformLocationService> { 
            AndroidLocationService() 
        }
        
        // Clock-in location service  
        single<LocationService> { 
            LocationService(
                context = androidContext()
            ) 
        }
    }