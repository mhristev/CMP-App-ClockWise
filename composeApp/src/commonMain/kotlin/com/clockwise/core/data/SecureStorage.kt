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
    fun getRefreshToken(): String?
    fun getUserRole(): UserRole?
    fun getUser(): User?
    fun saveUser(user: User)
    fun isTokenExpired(): Boolean
    fun clearAuthData()
    fun clearAllData()
}

/**
 * KVault implementation of secure storage
 */
class KVaultSecureStorage(
    private val vault: com.liftric.kvault.KVault,
    private val json: Json
) : SecureStorage {
    
    init {
        println("🔐 KVaultSecureStorage initialized with vault instance: ${vault.hashCode()}")
    }
    
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER = "user_data"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
    }
    
    override fun saveAuthData(authResponse: AuthResponse) {
        println("🔐 SecureStorage: Saving auth data to vault instance: ${vault.hashCode()}")
        vault.set(KEY_AUTH_TOKEN, authResponse.accessToken)
        vault.set(KEY_REFRESH_TOKEN, authResponse.refreshToken)
        vault.set(KEY_USER_ROLE, authResponse.role)
        
        // Calculate and store expiry time
        val currentTimeMillis = Clock.System.now().toEpochMilliseconds()
        val expiryTimeMillis = currentTimeMillis + (authResponse.expiresIn * 1000)
        vault.set(KEY_TOKEN_EXPIRY, expiryTimeMillis.toString())
        println("✅ SecureStorage: Auth data saved")
    }
    
    override fun getAuthToken(): String? {
        val token = if (isTokenExpired()) null else vault.string(KEY_AUTH_TOKEN)
        println("🔍 SecureStorage: Getting auth token from vault instance: ${vault.hashCode()}, exists: ${token != null}")
        return token
    }
    
    override fun getRefreshToken(): String? {
        return vault.string(KEY_REFRESH_TOKEN)
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
        val userJson = vault.string(KEY_USER)
        println("🔍 SecureStorage: Getting user from vault instance: ${vault.hashCode()}, exists: ${userJson != null}")
        return if (userJson == null) {
            null
        } else {
            try {
                json.decodeFromString<User>(userJson)
            } catch (e: Exception) {
                println("❌ SecureStorage: Error decoding user: ${e.message}")
                null
            }
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
        println("🧹 SecureStorage: Starting clearAuthData()")
        try {
            println("🔍 SecureStorage: Before clearing - checking if data exists:")
            println("   - Auth token exists: ${vault.string(KEY_AUTH_TOKEN) != null}")
            println("   - Refresh token exists: ${vault.string(KEY_REFRESH_TOKEN) != null}")
            println("   - User role exists: ${vault.string(KEY_USER_ROLE) != null}")
            println("   - User data exists: ${vault.string(KEY_USER) != null}")
            println("   - Token expiry exists: ${vault.string(KEY_TOKEN_EXPIRY) != null}")
            
            vault.deleteObject(KEY_AUTH_TOKEN)
            vault.deleteObject(KEY_REFRESH_TOKEN)
            vault.deleteObject(KEY_USER_ROLE)
            vault.deleteObject(KEY_USER)
            vault.deleteObject(KEY_TOKEN_EXPIRY)
            
            println("🔍 SecureStorage: After clearing - verifying data is gone:")
            println("   - Auth token exists: ${vault.string(KEY_AUTH_TOKEN) != null}")
            println("   - Refresh token exists: ${vault.string(KEY_REFRESH_TOKEN) != null}")
            println("   - User role exists: ${vault.string(KEY_USER_ROLE) != null}")
            println("   - User data exists: ${vault.string(KEY_USER) != null}")
            println("   - Token expiry exists: ${vault.string(KEY_TOKEN_EXPIRY) != null}")
            
            println("✅ SecureStorage: clearAuthData() completed")
        } catch (e: Exception) {
            println("❌ SecureStorage: Error during clearAuthData(): ${e.message}")
            e.printStackTrace()
        }
    }
    
    override fun clearAllData() {
        println("🧹 SecureStorage: Starting clearAllData()")
        try {
            println("🔍 SecureStorage: Before clearing - KVault instance: ${vault.hashCode()}")
            
            // First try individual deletion
            clearAuthData()
            
            // Then try vault.clear() as a nuclear option
            println("🔥 SecureStorage: Calling vault.clear() as nuclear option")
            vault.clear()
            
            println("🔍 SecureStorage: After vault.clear() - verifying everything is gone:")
            println("   - Auth token exists: ${vault.string(KEY_AUTH_TOKEN) != null}")
            println("   - Refresh token exists: ${vault.string(KEY_REFRESH_TOKEN) != null}")
            println("   - User role exists: ${vault.string(KEY_USER_ROLE) != null}")
            println("   - User data exists: ${vault.string(KEY_USER) != null}")
            println("   - Token expiry exists: ${vault.string(KEY_TOKEN_EXPIRY) != null}")
            
            // Try to check if KVault has any keys at all
            try {
                val authToken = vault.string(KEY_AUTH_TOKEN)
                val refreshToken = vault.string(KEY_REFRESH_TOKEN)
                if (authToken != null || refreshToken != null) {
                    println("❌ CRITICAL: Data still exists after clearing! This is the bug!")
                    println("   - Auth token: ${authToken?.take(20)}...")
                    println("   - Refresh token: ${refreshToken?.take(20)}...")
                } else {
                    println("✅ SecureStorage: All data successfully cleared")
                }
            } catch (e: Exception) {
                println("❌ SecureStorage: Error verifying clear: ${e.message}")
            }
            
            println("✅ SecureStorage: clearAllData() completed")
        } catch (e: Exception) {
            println("❌ SecureStorage: Error during clearAllData(): ${e.message}")
            e.printStackTrace()
        }
    }
}