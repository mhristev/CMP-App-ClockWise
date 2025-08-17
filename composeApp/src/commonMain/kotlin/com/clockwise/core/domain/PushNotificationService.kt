package com.clockwise.core.domain

/**
 * Result of FCM token operations
 */
sealed class TokenResult {
    data class Success(val token: String) : TokenResult()
    data class Error(val message: String) : TokenResult()
    data object NotAvailable : TokenResult()
}

/**
 * Push notification permission states
 */
sealed class NotificationPermissionStatus {
    data object Granted : NotificationPermissionStatus()
    data object Denied : NotificationPermissionStatus()
    data object NotDetermined : NotificationPermissionStatus()
    data object NotSupported : NotificationPermissionStatus()
}

/**
 * Represents a received push notification
 */
data class PushNotificationData(
    val type: String,
    val postId: String? = null,
    val businessUnitId: String? = null,
    val authorName: String? = null,
    val createdAt: String? = null,
    val title: String? = null,
    val body: String? = null
)

/**
 * Platform-agnostic push notification service interface
 * Provides methods for handling FCM tokens, permissions, and notifications
 */
interface PushNotificationService {
    
    /**
     * Initializes the push notification service
     * Should be called when the app starts
     */
    suspend fun initialize()
    
    /**
     * Gets the current FCM token
     * Returns null if not available or not supported on the platform
     */
    suspend fun getFcmToken(): TokenResult
    
    /**
     * Refreshes the FCM token
     * Useful when the token needs to be updated
     */
    suspend fun refreshFcmToken(): TokenResult
    
    /**
     * Requests notification permissions from the user
     * On iOS, this will show the system permission dialog
     * On Android, this handles the permission request flow
     */
    suspend fun requestNotificationPermission(): NotificationPermissionStatus
    
    /**
     * Checks if notification permissions are granted
     */
    suspend fun checkNotificationPermission(): NotificationPermissionStatus
    
    /**
     * Sets up notification handling callbacks
     * Called when a notification is received while app is in foreground
     */
    fun setNotificationReceivedCallback(callback: (PushNotificationData) -> Unit)
    
    /**
     * Sets up notification clicked callbacks
     * Called when user taps on a notification
     */
    fun setNotificationClickedCallback(callback: (PushNotificationData) -> Unit)
    
    /**
     * Handles notification received while app is in background
     * Called when app is opened from a notification
     */
    fun handleBackgroundNotification(data: Map<String, String>): PushNotificationData?
    
    /**
     * Subscribes to a topic (optional feature)
     * Can be used for broadcast notifications
     */
    suspend fun subscribeToTopic(topic: String): Boolean
    
    /**
     * Unsubscribes from a topic
     */
    suspend fun unsubscribeFromTopic(topic: String): Boolean
    
    /**
     * Checks if push notifications are supported on this platform
     */
    fun isSupported(): Boolean
}