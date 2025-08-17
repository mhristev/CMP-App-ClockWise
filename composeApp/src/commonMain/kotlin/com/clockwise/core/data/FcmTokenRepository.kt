package com.clockwise.core.data

import com.clockwise.core.domain.PushNotificationService
import com.clockwise.core.domain.TokenResult
import com.plcoding.bookpedia.core.presentation.UiText
import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing FCM tokens
 * Handles token retrieval, storage, and synchronization with backend
 */
class FcmTokenRepository(
    private val pushNotificationService: PushNotificationService,
    private val httpClient: HttpClient,
    private val vault: com.liftric.kvault.KVault,
    private val userService: com.clockwise.features.auth.UserService,
    private val apiConfig: com.clockwise.core.di.ApiConfig
) {
    
    private val _tokenState = MutableStateFlow<TokenState>(TokenState.NotInitialized)
    val tokenState: StateFlow<TokenState> = _tokenState.asStateFlow()
    
    companion object {
        private const val FCM_TOKEN_KEY = "fcm_token"
        private const val TOKEN_SYNCED_KEY = "fcm_token_synced"
    }
    
    /**
     * States of FCM token management
     */
    sealed class TokenState {
        data object NotInitialized : TokenState()
        data object Loading : TokenState()
        data class Success(val token: String, val synced: Boolean) : TokenState()
        data class Error(val message: UiText) : TokenState()
        data object NotSupported : TokenState()
    }
    
    /**
     * Initializes FCM token management
     * Gets token from FCM and syncs with backend if needed
     */
    suspend fun initialize() {
        println("🔔 FCM: Starting FCM token initialization...")
        
        if (!pushNotificationService.isSupported()) {
            println("❌ FCM: Push notifications not supported on this platform")
            _tokenState.value = TokenState.NotSupported
            return
        }
        
        _tokenState.value = TokenState.Loading
        
        try {
            // Initialize push notification service
            println("🔔 FCM: Initializing push notification service...")
            pushNotificationService.initialize()
            
            // Get FCM token
            println("🔔 FCM: Requesting FCM token from service...")
            when (val tokenResult = pushNotificationService.getFcmToken()) {
                is TokenResult.Success -> {
                    val token = tokenResult.token
                    println("✅ FCM: Token received: ${token.take(20)}...")
                    
                    val storedToken = vault.string(FCM_TOKEN_KEY)
                    var isTokenSynced = vault.string(TOKEN_SYNCED_KEY)?.toBoolean() == true
                    
                    // Store token locally if different
                    if (storedToken != token) {
                        println("🔔 FCM: New token detected, storing locally...")
                        vault.set(FCM_TOKEN_KEY, token)
                        vault.set(TOKEN_SYNCED_KEY, "false")
                        isTokenSynced = false
                    } else {
                        println("🔔 FCM: Token unchanged from stored version")
                    }
                    
                    _tokenState.value = TokenState.Success(token, isTokenSynced)
                    
                    // Sync with backend if not synced
                    if (!isTokenSynced) {
                        println("🔄 FCM: Token not synced with backend, initiating sync...")
                        syncTokenWithBackend(token)
                    } else {
                        println("✅ FCM: Token already synced with backend")
                    }
                }
                is TokenResult.Error -> {
                    println("❌ FCM: Error getting token: ${tokenResult.message}")
                    _tokenState.value = TokenState.Error(UiText.DynamicString(tokenResult.message))
                }
                is TokenResult.NotAvailable -> {
                    println("❌ FCM: Token not available")
                    _tokenState.value = TokenState.NotSupported
                }
            }
        } catch (e: Exception) {
            println("❌ FCM: Exception during initialization: ${e.message}")
            e.printStackTrace()
            _tokenState.value = TokenState.Error(UiText.DynamicString("Failed to initialize push notifications"))
        }
    }
    
    /**
     * Refreshes the FCM token and syncs with backend
     */
    suspend fun refreshToken() {
        if (!pushNotificationService.isSupported()) {
            return
        }
        
        _tokenState.value = TokenState.Loading
        
        try {
            when (val tokenResult = pushNotificationService.refreshFcmToken()) {
                is TokenResult.Success -> {
                    val token = tokenResult.token
                    vault.set(FCM_TOKEN_KEY, token)
                    vault.set(TOKEN_SYNCED_KEY, "false")
                    
                    _tokenState.value = TokenState.Success(token, false)
                    syncTokenWithBackend(token)
                }
                is TokenResult.Error -> {
                    _tokenState.value = TokenState.Error(UiText.DynamicString(tokenResult.message))
                }
                is TokenResult.NotAvailable -> {
                    _tokenState.value = TokenState.NotSupported
                }
            }
        } catch (e: Exception) {
            println("Error refreshing FCM token: ${e.message}")
            _tokenState.value = TokenState.Error(UiText.DynamicString("Failed to refresh push notification token"))
        }
    }
    
    /**
     * Gets the current FCM token from local storage
     */
    fun getCurrentToken(): String? {
        return vault.string(FCM_TOKEN_KEY)
    }
    
    /**
     * Syncs FCM token with backend
     */
    suspend fun syncTokenWithBackend(token: String? = null) {
        val fcmToken = token ?: getCurrentToken()
        if (fcmToken == null) {
            println("❌ FCM: No FCM token available to sync")
            return
        }
        
        try {
            // Get current user ID (this would come from auth context)
            val currentUserId = getCurrentUserId()
            if (currentUserId == null) {
                println("❌ FCM: No current user ID available - user not logged in")
                return
            }
            
            println("🔄 FCM: Starting token sync for user $currentUserId")
            println("🔄 FCM: Token to sync: ${fcmToken.take(20)}...")
            
            // Get valid auth token
            val authToken = userService.getValidAuthToken()
            if (authToken == null) {
                println("❌ FCM: No valid auth token - cannot sync FCM token")
                return
            }
            
            println("🔄 FCM: Using auth token: ${authToken.take(30)}...")
            println("🔄 FCM: Syncing to URL: ${apiConfig.baseUsersUrl}/$currentUserId/fcm-token")
            
            // Send token to backend using the User Service endpoint
            val response = httpClient.put("${apiConfig.baseUsersUrl}/$currentUserId/fcm-token") {
                contentType(ContentType.Application.Json)
                headers {
                    append("Authorization", "Bearer $authToken")
                }
                setBody(mapOf("fcmToken" to fcmToken))
            }
            
            println("🔄 FCM: Response status: ${response.status.value}")
            
            if (response.status.value in 200..299) {
                vault.set(TOKEN_SYNCED_KEY, "true")
                
                // Update state if this is the current token
                if (getCurrentToken() == fcmToken) {
                    _tokenState.value = TokenState.Success(fcmToken, true)
                }
                
                println("✅ FCM: Token synced with backend successfully")
            } else {
                println("❌ FCM: Failed to sync token - HTTP ${response.status.value}")
                val responseBody = try { response.bodyAsText() } catch (e: Exception) { "Could not read response body" }
                println("❌ FCM: Response body: $responseBody")
            }
            
        } catch (e: Exception) {
            println("❌ FCM: Exception during token sync: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * Clears FCM token (called on logout)
     */
    suspend fun clearToken() {
        try {
            vault.deleteObject(FCM_TOKEN_KEY)
            vault.deleteObject(TOKEN_SYNCED_KEY)
            _tokenState.value = TokenState.NotInitialized
            
            println("FCM token cleared")
        } catch (e: Exception) {
            println("Error clearing FCM token: ${e.message}")
        }
    }
    
    /**
     * Gets current user ID from auth context
     */
    private fun getCurrentUserId(): String? {
        return userService.currentUser.value?.id
    }
}