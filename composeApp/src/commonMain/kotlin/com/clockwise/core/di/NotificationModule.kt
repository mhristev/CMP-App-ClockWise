package com.clockwise.core.di

import com.clockwise.core.data.FcmTokenRepository
import com.clockwise.core.domain.PushNotificationService
import com.clockwise.features.notifications.domain.service.FCMInitializationService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Dependency injection module for push notification related services
 */
val notificationModule = module {
    
    // Platform-specific PushNotificationService will be provided by platform modules
    // Android: AndroidPushNotificationService
    // iOS: IOSPushNotificationService
    
    // FCM Token Repository
    single {
        FcmTokenRepository(
            pushNotificationService = get(),
            httpClient = get(),
            vault = get(),
            userService = get(),
            apiConfig = get()
        )
    }
    
    // FCM Initialization Service
    single<FCMInitializationService> { 
        FCMInitializationService(get())
    }
    
    // Notification Handler
    single { 
        com.clockwise.core.data.NotificationHandler(
            pushNotificationService = get()
        )
    }
    
}