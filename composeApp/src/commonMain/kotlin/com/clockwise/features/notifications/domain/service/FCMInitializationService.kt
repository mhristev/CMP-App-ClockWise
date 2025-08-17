package com.clockwise.features.notifications.domain.service

import com.clockwise.core.data.FcmTokenRepository

/**
 * Service responsible for initializing FCM tokens after user authentication
 */
class FCMInitializationService(
    private val fcmTokenRepository: FcmTokenRepository
) {
    
    /**
     * Initialize FCM token after user login
     * This should be called after successful authentication
     */
    suspend fun initializeAfterLogin() {
        try {
            println("🔔 FCMInitializationService: Starting FCM initialization after login")
            fcmTokenRepository.initialize()
            println("✅ FCMInitializationService: FCM initialization completed")
        } catch (e: Exception) {
            println("❌ FCMInitializationService: Failed to initialize FCM token: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * Sync FCM token with backend after user authentication
     * This ensures the token is properly stored in the backend database
     */
    suspend fun syncTokenWithBackend() {
        try {
            println("🔄 FCMInitializationService: Starting FCM token sync with backend")
            fcmTokenRepository.syncTokenWithBackend()
            println("✅ FCMInitializationService: FCM token sync completed")
        } catch (e: Exception) {
            println("❌ FCMInitializationService: Failed to sync FCM token: ${e.message}")
            e.printStackTrace()
        }
    }
}
