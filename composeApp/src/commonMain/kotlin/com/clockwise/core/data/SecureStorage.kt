package com.clockwise.core.data

import com.clockwise.core.model.User
import com.clockwise.core.model.UserRole
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
    fun getUserRole(): UserRole?
    fun getUser(): User?
    fun saveUser(user: User)
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
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER = "user_data"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
    }
    
    override fun saveAuthData(authResponse: AuthResponse) {
        vault.set(KEY_AUTH_TOKEN, authResponse.accessToken)
        vault.set(KEY_REFRESH_TOKEN, authResponse.refreshToken)
        vault.set(KEY_USER_ROLE, authResponse.role)
        
        // Calculate and store expiry time
        val currentTimeMillis = Clock.System.now().toEpochMilliseconds()
        val expiryTimeMillis = currentTimeMillis + (authResponse.expiresIn * 1000)
        vault.set(KEY_TOKEN_EXPIRY, expiryTimeMillis.toString())
    }
    
    override fun getAuthToken(): String? {
        return if (isTokenExpired()) null else vault.string(KEY_AUTH_TOKEN)
    }
    
    override fun getUserRole(): UserRole? {
        val roleString = vault.string(KEY_USER_ROLE) ?: return null
        return try {
            UserRole.valueOf(roleString)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    override fun getUser(): User? {
        val userJson = vault.string(KEY_USER) ?: return null
        return try {
            json.decodeFromString<User>(userJson)
        } catch (e: Exception) {
            null
        }
    }
    
    override fun saveUser(user: User) {
        val userJson = json.encodeToString(user)
        vault.set(KEY_USER, userJson)
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
        vault.deleteObject(KEY_USER_ROLE)
        vault.deleteObject(KEY_USER)
        vault.deleteObject(KEY_TOKEN_EXPIRY)
    }
}