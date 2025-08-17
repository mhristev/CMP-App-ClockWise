package com.clockwise.core.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.clockwise.core.domain.NotificationPermissionStatus
import com.clockwise.core.domain.PushNotificationData
import com.clockwise.core.domain.PushNotificationService
import com.clockwise.core.domain.TokenResult
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

/**
 * Android implementation of PushNotificationService using Firebase Cloud Messaging
 */
class AndroidPushNotificationService(
    private val context: Context
) : PushNotificationService {

    private var notificationReceivedCallback: ((PushNotificationData) -> Unit)? = null
    private var notificationClickedCallback: ((PushNotificationData) -> Unit)? = null
    
    companion object {
        private const val TAG = "AndroidPushNotification"
    }

    override suspend fun initialize() {
        try {
            // Initialize Firebase Messaging
            FirebaseMessaging.getInstance().isAutoInitEnabled = true
            Log.d(TAG, "Firebase Messaging initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase Messaging", e)
        }
    }

    override suspend fun getFcmToken(): TokenResult {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "FCM Token retrieved: ${token.take(10)}...")
            TokenResult.Success(token)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get FCM token", e)
            TokenResult.Error(e.message ?: "Unknown error getting FCM token")
        }
    }

    override suspend fun refreshFcmToken(): TokenResult {
        return try {
            FirebaseMessaging.getInstance().deleteToken().await()
            val newToken = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "FCM Token refreshed: ${newToken.take(10)}...")
            TokenResult.Success(newToken)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to refresh FCM token", e)
            TokenResult.Error(e.message ?: "Unknown error refreshing FCM token")
        }
    }

    override suspend fun requestNotificationPermission(): NotificationPermissionStatus {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires runtime permission for notifications
            when {
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    NotificationPermissionStatus.Granted
                }
                else -> {
                    // Permission needs to be requested by the Activity
                    // This service cannot request permissions directly
                    NotificationPermissionStatus.NotDetermined
                }
            }
        } else {
            // Below Android 13, notifications are granted by default
            NotificationPermissionStatus.Granted
        }
    }

    override suspend fun checkNotificationPermission(): NotificationPermissionStatus {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    NotificationPermissionStatus.Granted
                }
                else -> NotificationPermissionStatus.Denied
            }
        } else {
            NotificationPermissionStatus.Granted
        }
    }

    override fun setNotificationReceivedCallback(callback: (PushNotificationData) -> Unit) {
        notificationReceivedCallback = callback
    }

    override fun setNotificationClickedCallback(callback: (PushNotificationData) -> Unit) {
        notificationClickedCallback = callback
    }

    override fun handleBackgroundNotification(data: Map<String, String>): PushNotificationData? {
        return try {
            val notificationData = PushNotificationData(
                type = data["type"] ?: "unknown",
                postId = data["postId"],
                businessUnitId = data["businessUnitId"],
                authorName = data["authorName"],
                createdAt = data["createdAt"],
                title = data["title"],
                body = data["body"]
            )
            
            Log.d(TAG, "Handling background notification: $notificationData")
            notificationData
        } catch (e: Exception) {
            Log.e(TAG, "Error handling background notification", e)
            null
        }
    }

    override suspend fun subscribeToTopic(topic: String): Boolean {
        return try {
            FirebaseMessaging.getInstance().subscribeToTopic(topic).await()
            Log.d(TAG, "Subscribed to topic: $topic")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to subscribe to topic: $topic", e)
            false
        }
    }

    override suspend fun unsubscribeFromTopic(topic: String): Boolean {
        return try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).await()
            Log.d(TAG, "Unsubscribed from topic: $topic")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unsubscribe from topic: $topic", e)
            false
        }
    }

    override fun isSupported(): Boolean = true

    /**
     * Called by FirebaseMessagingService when a notification is received
     */
    fun onNotificationReceived(data: PushNotificationData) {
        Log.d(TAG, "Notification received: $data")
        notificationReceivedCallback?.invoke(data)
    }

    /**
     * Called when user clicks on a notification
     */
    fun onNotificationClicked(data: PushNotificationData) {
        Log.d(TAG, "Notification clicked: $data")
        notificationClickedCallback?.invoke(data)
    }
}