package com.clockwise.features.auth

import com.clockwise.core.data.SecureStorage
import com.clockwise.core.model.User
import com.clockwise.core.model.UserRole
import com.clockwise.features.auth.domain.model.AuthResponse
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.DataError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UserService(
    private val secureStorage: SecureStorage
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    private val _currentUserRole = MutableStateFlow<UserRole?>(null)
    val currentUserRole: StateFlow<UserRole?> = _currentUserRole.asStateFlow()

    // Refresh token functionality
    private val refreshMutex = Mutex()
    private var refreshTokenFunction: (suspend (String) -> Result<AuthResponse, DataError.Remote>)? = null

    init {
        loadStoredData()
    }

    fun setRefreshTokenFunction(refreshFunction: suspend (String) -> Result<AuthResponse, DataError.Remote>) {
        refreshTokenFunction = refreshFunction
    }

    suspend fun getValidAuthToken(): String? {
        val currentToken = _authToken.value
        if (currentToken.isNullOrBlank()) {
            return null
        }

        // Use SecureStorage's built-in token expiry check
        if (secureStorage.isTokenExpired()) {
            println("⏰ Token expired, attempting refresh...")
            return refreshTokenIfNeeded()
        }

        return currentToken
    }

    private suspend fun refreshTokenIfNeeded(): String? {
        return refreshMutex.withLock {
            val currentToken = _authToken.value
            
            // Double check if token is still expired after acquiring lock
            if (currentToken != null && !secureStorage.isTokenExpired()) {
                return@withLock currentToken
            }

            val refreshToken = secureStorage.getRefreshToken()
            if (refreshToken.isNullOrBlank()) {
                println("❌ No refresh token available, clearing user data")
                clearAllUserData()
                return@withLock null
            }

            try {
                println("🔄 Starting token refresh...")
                refreshTokenInternal(refreshToken)
            } catch (e: Exception) {
                println("❌ Token refresh failed: ${e.message}")
                clearAllUserData()
                null
            }
        }
    }

    private suspend fun refreshTokenInternal(refreshToken: String): String? {
        val refreshFunction = refreshTokenFunction
            ?: throw IllegalStateException("Refresh token function not set")

        println("🔄 Calling refresh function with token: ${refreshToken.take(20)}...")
        return when (val result = refreshFunction(refreshToken)) {
            is Result.Success -> {
                println("✅ Token refresh successful!")
                saveAuthResponse(result.data)
                result.data.accessToken
            }
            is Result.Error -> {
                println("❌ Token refresh failed: ${result.error}")
                clearAllUserData()
                null
            }
        }
    }

    fun saveAuthResponse(authResponse: AuthResponse) {
        _authToken.value = authResponse.accessToken
        secureStorage.saveAuthData(authResponse)
    }

    fun saveUser(user: User) {
        _currentUser.value = user
        _currentUserRole.value = user.role
        secureStorage.saveUser(user)
    }

    suspend fun clearAllUserData() {
        _currentUser.value = null
        _authToken.value = null
        _currentUserRole.value = null
        secureStorage.clearAllData()
    }

    val isUserAuthorized: Boolean
        get() = _authToken.value != null && _currentUser.value != null

    private fun loadStoredData() {
        val storedUser = secureStorage.getUser()
        val storedRole = secureStorage.getUserRole()

        // Load user and role first
        if (storedUser != null) {
            _currentUser.value = storedUser
            println("✅ Loaded stored user: ${storedUser.email}")
        }

        if (storedRole != null) {
            _currentUserRole.value = storedRole
            println("✅ Loaded stored role: $storedRole")
        }

        // For token, use getAuthToken() which checks expiration
        val storedToken = secureStorage.getAuthToken()
        if (!storedToken.isNullOrBlank()) {
            _authToken.value = storedToken
            println("✅ Loaded valid stored token")
        } else {
            // Token is expired or null, but we might have a refresh token
            println("⚠️ Stored token is expired or missing on app startup")
        }
    }

    // Add this method to be called during app initialization
    suspend fun initializeAuthState() {
        // If we have user data but no valid token, try to refresh
        if (_currentUser.value != null && _authToken.value == null) {
            val refreshToken = secureStorage.getRefreshToken()
            if (!refreshToken.isNullOrBlank()) {
                println("🔄 Attempting to refresh token on app startup...")
                val newToken = refreshTokenIfNeeded()
                if (newToken != null) {
                    println("✅ Successfully refreshed token on startup")
                } else {
                    println("❌ Failed to refresh token on startup")
                }
            } else {
                println("❌ No refresh token available on startup")
                clearAllUserData()
            }
        }
    }
} 