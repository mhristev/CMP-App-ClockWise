package com.clockwise.core.data

import com.clockwise.core.domain.PushNotificationData
import com.clockwise.core.domain.PushNotificationService
import com.clockwise.app.navigation.NavigationRoutes

/**
 * Handles push notification actions like navigation and UI updates
 */
class NotificationHandler(
    private val pushNotificationService: PushNotificationService
) {
    
    private var navigationCallback: ((String) -> Unit)? = null
    private var postNotificationCallback: ((String) -> Unit)? = null
    
    fun initialize() {
        if (!pushNotificationService.isSupported()) {
            println("Push notifications not supported on this platform")
            return
        }
        
        // Set up notification received callback (foreground)
        pushNotificationService.setNotificationReceivedCallback { data ->
            handleNotificationReceived(data)
        }
        
        // Set up notification clicked callback (background/notification tray)
        pushNotificationService.setNotificationClickedCallback { data ->
            handleNotificationClicked(data)
        }
        
        println("Notification handler initialized")
    }
    
    /**
     * Sets navigation callback to handle deep linking
     */
    fun setNavigationCallback(callback: (String) -> Unit) {
        navigationCallback = callback
    }
    
    /**
     * Sets callback for post-specific notifications (like highlighting new posts)
     */
    fun setPostNotificationCallback(callback: (String) -> Unit) {
        postNotificationCallback = callback
    }
    
    /**
     * Handles notification received while app is in foreground
     */
    private fun handleNotificationReceived(data: PushNotificationData) {
        println("Notification received in foreground: ${data.type}")
        
        when (data.type) {
            "new_post" -> {
                data.postId?.let { postId ->
                    // Notify posts screen about new post (could show badge, refresh list, etc.)
                    postNotificationCallback?.invoke(postId)
                }
            }
            else -> {
                println("Unhandled notification type in foreground: ${data.type}")
            }
        }
    }
    
    /**
     * Handles notification clicked (user taps on notification)
     */
    private fun handleNotificationClicked(data: PushNotificationData) {
        println("Notification clicked: ${data.type}")
        
        when (data.type) {
            "new_post" -> {
                // Navigate to posts screen
                navigationCallback?.invoke(NavigationRoutes.Posts.route)
                
                // If we have a specific post ID, we could navigate directly to it
                data.postId?.let { postId ->
                    // For now, just notify the posts screen to highlight this post
                    postNotificationCallback?.invoke(postId)
                }
            }
            else -> {
                println("Unhandled notification type for click: ${data.type}")
            }
        }
    }
    
    /**
     * Handles notification from app launch (when app was closed and opened via notification)
     */
    fun handleLaunchNotification(data: Map<String, String>) {
        val notificationData = pushNotificationService.handleBackgroundNotification(data)
        if (notificationData != null) {
            println("Handling launch notification: ${notificationData.type}")
            handleNotificationClicked(notificationData)
        }
    }
    
    /**
     * Shows notification badge count (platform specific implementation needed)
     */
    fun updateBadgeCount(count: Int) {
        // This would need platform-specific implementation
        // On iOS: UIApplication.shared.applicationIconBadgeNumber
        // On Android: Custom badge implementation or third-party library
        println("Should update badge count to: $count")
    }
    
    /**
     * Clears all notifications (useful on app resume or when viewing posts)
     */
    fun clearNotifications() {
        updateBadgeCount(0)
        println("Cleared notifications")
    }
}