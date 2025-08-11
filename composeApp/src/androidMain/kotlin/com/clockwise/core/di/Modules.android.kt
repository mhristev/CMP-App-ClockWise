package com.clockwise.core.di

import android.content.Context
import androidx.activity.ComponentActivity
import com.clockwise.core.data.AndroidPlatformDataCleaner
import com.clockwise.core.data.PlatformDataCleaner
import com.clockwise.features.clockin.domain.service.LocationService
import com.clockwise.features.clockin.data.service.AndroidLocationServiceImpl
import com.clockwise.features.sidemenu.platform.PlatformActions
import com.clockwise.features.sidemenu.platform.AndroidPlatformActions
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
            val vault = KVault(context, "clockwise_secure_storage")
            println("🔐 Android: Created KVault instance: ${vault.hashCode()}")
            vault
        }
        
        // Clock-in location service (real GPS implementation - overrides mock)
        single<LocationService> { 
            AndroidLocationServiceImpl(androidContext()) 
        }
        
        // Platform-specific actions for side menu
        single<PlatformActions> {
            AndroidPlatformActions(androidContext())
        }
        
        // Android-specific data cleaner
        single<PlatformDataCleaner> {
            AndroidPlatformDataCleaner(androidContext())
        }
    }