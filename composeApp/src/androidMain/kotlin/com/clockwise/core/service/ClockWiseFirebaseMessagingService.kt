package com.clockwise.core.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.clockwise.MainActivity
import com.clockwise.R
import com.clockwise.core.domain.PushNotificationData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Firebase Cloud Messaging service for handling push notifications
 */
class ClockWiseFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "clockwise_notifications"
        private const val CHANNEL_NAME = "ClockWise Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications from ClockWise app"
        
        // Static reference to handle notification callbacks
        var notificationReceivedCallback: ((PushNotificationData) -> Unit)? = null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d(TAG, "Firebase Messaging Service created")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token generated: ${token.take(10)}...")
        
        // Send token to server
        // This should be handled by the app when it starts up
        // We could also store it locally and send it when the app is ready
        sendTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "Message received from: ${remoteMessage.from}")
        
        // Handle notification payload
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Notification title: ${notification.title}")
            Log.d(TAG, "Notification body: ${notification.body}")
        }

        // Handle data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        // Show notification if app is in foreground or background
        showNotification(remoteMessage)
    }

    private fun handleDataMessage(data: Map<String, String>) {
        try {
            val notificationData = PushNotificationData(
                type = data["type"] ?: "unknown",
                postId = data["postId"],
                businessUnitId = data["businessUnitId"],
                authorName = data["authorName"],
                createdAt = data["createdAt"],
                title = data["title"],
                body = data["body"]
            )
            
            Log.d(TAG, "Handling data message: $notificationData")
            
            // Notify the app about the received notification
            notificationReceivedCallback?.invoke(notificationData)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error handling data message", e)
        }
    }

    private fun showNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "ClockWise"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "New notification"
        
        // Create intent for when notification is clicked
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            
            // Add notification data as extras
            remoteMessage.data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 
            System.currentTimeMillis().toInt(),
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // You may need to add this icon
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            
            Log.d(TAG, "Notification channel created: $CHANNEL_ID")
        }
    }

    private fun sendTokenToServer(token: String) {
        // This will be handled by the app's token management system
        Log.d(TAG, "Should send token to server: ${token.take(10)}...")
        // Implementation will be added in the token management service
    }
}