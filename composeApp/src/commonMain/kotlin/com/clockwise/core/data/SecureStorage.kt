package com.clockwise.core.data

import com.clockwise.core.model.User
import com.clockwise.features.auth.domain.model.AuthResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.datetime.Clock

/**
 * Interface for secure storage operations
 */
interface SecureStorage {
    fun saveAuthData(authResponse: AuthResponse)
    fun getAuthToken(): String?
    fun getUser(): User?
    fun isTokenExpired(): Boolean
    fun clearAuthData()
}

/**
 * KVault implementation of secure storage
 */
class KVaultSecureStorage(
    private val vault: com.liftric.kvault.KVault,
    private val json: Json
) : SecureStorage {
    
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER = "user_data"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
    }
    
    override fun saveAuthData(authResponse: AuthResponse) {
        vault.set(KEY_AUTH_TOKEN, authResponse.token)
        vault.set(KEY_REFRESH_TOKEN, authResponse.refreshToken)
        
        // Calculate and store expiry time
        val currentTimeMillis = Clock.System.now().toEpochMilliseconds()
        val expiryTimeMillis = currentTimeMillis + (authResponse.expiresIn * 1000)
        vault.set(KEY_TOKEN_EXPIRY, expiryTimeMillis.toString())
        
        // Store serialized user
        val userJson = json.encodeToString(authResponse.user)
        vault.set(KEY_USER, userJson)
    }
    
    override fun getAuthToken(): String? {
        return if (isTokenExpired()) null else vault.string(KEY_AUTH_TOKEN)
    }
    
    override fun getUser(): User? {
        val userJson = vault.string(KEY_USER) ?: return null
        return try {
            json.decodeFromString<User>(userJson)
        } catch (e: Exception) {
            null
        }
    }
    
    override fun isTokenExpired(): Boolean {
        val expiryTimeStr = vault.string(KEY_TOKEN_EXPIRY) ?: return true
        val expiryTimeMillis = expiryTimeStr.toLongOrNull() ?: return true
        val currentTimeMillis = Clock.System.now().toEpochMilliseconds()
        
        return currentTimeMillis >= expiryTimeMillis
    }
    
    override fun clearAuthData() {
        vault.deleteObject(KEY_AUTH_TOKEN)
        vault.deleteObject(KEY_REFRESH_TOKEN)
        vault.deleteObject(KEY_USER)
        vault.deleteObject(KEY_TOKEN_EXPIRY)
    }
} 