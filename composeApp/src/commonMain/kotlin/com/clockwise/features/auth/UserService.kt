package com.clockwise.features.auth

import com.clockwise.core.data.SecureStorage
import com.clockwise.core.data.DataClearingService
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
    private val secureStorage: SecureStorage,
    private val dataClearingService: DataClearingService
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
    
    // Flag to prevent automatic loading after clearing
    private var preventAutoLoad = false

    init {
        loadStoredData()
    }

    fun setRefreshTokenFunction(refreshFunction: suspend (String) -> Result<AuthResponse, DataError.Remote>) {
        refreshTokenFunction = refreshFunction
    }

    suspend fun getValidAuthToken(): String? {
        val currentToken = _authToken.value
        if (currentToken.isNullOrBlank()) {
            println("❌ getValidAuthToken: No token in memory")
            return null
        }

        println("🔍 getValidAuthToken: Current token: ${currentToken.take(20)}... (length: ${currentToken.length})")
        
        // Use SecureStorage's built-in token expiry check
        val isExpired = secureStorage.isTokenExpired()
        println("🔍 getValidAuthToken: Token expired? $isExpired")
        
        if (isExpired) {
            println("⏰ Token expired, attempting refresh...")
            val refreshedToken = refreshTokenIfNeeded()
            println("🔄 getValidAuthToken: After refresh: ${refreshedToken?.take(20)}... (length: ${refreshedToken?.length})")
            return refreshedToken
        }

        println("✅ getValidAuthToken: Returning current token (not expired)")
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
        println("🔐 UserService: Saving auth response...")
        println("🔍 NEW TOKEN: ${authResponse.accessToken.take(30)}... (length: ${authResponse.accessToken.length})")
        preventAutoLoad = false // Re-enable auto loading for new user
        _authToken.value = authResponse.accessToken
        secureStorage.saveAuthData(authResponse)
        println("✅ UserService: Auth response saved - token in memory: ${_authToken.value?.take(30)}...")
    }

    fun saveUser(user: User) {
        println("👤 UserService: Saving user data...")
        preventAutoLoad = false // Re-enable auto loading for new user
        _currentUser.value = user
        _currentUserRole.value = user.role
        secureStorage.saveUser(user)
        println("✅ UserService: User data saved: ${user.email}")
    }

    suspend fun clearAllUserData() {
        println("🧹 UserService: Starting comprehensive user data clearing...")
        
        // Set flag to prevent automatic reloading
        preventAutoLoad = true
        
        // Debug current state before clearing
        println("🔍 UserService: Before clearing - current state:")
        println("   - Current user: ${_currentUser.value?.email}")
        println("   - Auth token exists: ${_authToken.value != null}")
        println("   - User role: ${_currentUserRole.value}")
        
        // Clear in-memory state first
        println("🧹 UserService: Clearing in-memory state...")
        _currentUser.value = null
        _authToken.value = null
        _currentUserRole.value = null
        
        println("🔍 UserService: After in-memory clearing:")
        println("   - Current user: ${_currentUser.value}")
        println("   - Auth token: ${_authToken.value}")
        println("   - User role: ${_currentUserRole.value}")
        
        // Use the comprehensive data clearing service
        println("🧹 UserService: Calling dataClearingService.clearAllUserData()...")
        try {
            dataClearingService.clearAllUserData()
            println("✅ UserService: dataClearingService.clearAllUserData() completed")
        } catch (e: Exception) {
            println("❌ UserService: Error in dataClearingService.clearAllUserData(): ${e.message}")
            e.printStackTrace()
        }
        
        // CRITICAL: Force clear the secure storage directly as a backup
        println("🔥 UserService: FORCE CLEARING secure storage directly as backup...")
        try {
            secureStorage.clearAllData()
            println("✅ UserService: Direct secure storage clearing completed")
        } catch (e: Exception) {
            println("❌ UserService: Error in direct secure storage clearing: ${e.message}")
            e.printStackTrace()
        }
        
        // Verify clearing worked by checking if we can still get stored data
        println("🔍 UserService: Verifying clearing by checking stored data...")
        try {
            val storedUser = secureStorage.getUser()
            val storedToken = secureStorage.getAuthToken()
            val storedRole = secureStorage.getUserRole()
            
            if (storedUser != null || storedToken != null || storedRole != null) {
                println("❌ CRITICAL BUG: Data still exists after clearing!")
                println("   - Stored user: ${storedUser?.email}")
                println("   - Stored token exists: ${storedToken != null}")
                println("   - Stored role: ${storedRole}")
                
                // NUCLEAR OPTION: Try individual key deletion
                println("🔥 NUCLEAR OPTION: Trying individual key deletion...")
                secureStorage.clearAuthData()
            } else {
                println("✅ UserService: Verification passed - no stored data found")
            }
        } catch (e: Exception) {
            println("❌ UserService: Error during verification: ${e.message}")
        }
        
        // Reset the prevent flag after some delay
        preventAutoLoad = false
        
        println("✅ UserService: User data clearing completed")
    }

    val isUserAuthorized: Boolean
        get() = _authToken.value != null && _currentUser.value != null

    private fun loadStoredData() {
        if (preventAutoLoad) {
            println("🚫 UserService: Skipping loadStoredData() due to preventAutoLoad flag")
            return
        }
        
        println("🔍 UserService: Loading stored data...")
        val storedUser = secureStorage.getUser()
        val storedRole = secureStorage.getUserRole()

        // Load user and role first
        if (storedUser != null) {
            _currentUser.value = storedUser
            println("✅ Loaded stored user: ${storedUser.email}")
        } else {
            println("ℹ️ No stored user found")
        }

        if (storedRole != null) {
            _currentUserRole.value = storedRole
            println("✅ Loaded stored role: $storedRole")
        } else {
            println("ℹ️ No stored role found")
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
    
    /**
     * Debug method to check current token and user state
     * Call this to verify UserService has the correct user/token after login
     */
    fun debugCurrentState() {
        println("🔍 UserService DEBUG STATE:")
        println("   - Current user email: ${_currentUser.value?.email}")
        println("   - Current user ID: ${_currentUser.value?.id}")
        println("   - Current user name: ${_currentUser.value?.firstName} ${_currentUser.value?.lastName}")
        println("   - Current user role: ${_currentUserRole.value}")
        println("   - Auth token exists: ${_authToken.value != null}")
        println("   - Auth token preview: ${_authToken.value?.take(50)}...")
        println("   - Auth token length: ${_authToken.value?.length}")
        println("   - PreventAutoLoad flag: $preventAutoLoad")
        println("   - User authorized: $isUserAuthorized")
        
        // Also check stored data
        val storedUser = secureStorage.getUser()
        val storedToken = secureStorage.getAuthToken()
        val storedRole = secureStorage.getUserRole()
        
        println("🔍 UserService STORED DATA:")
        println("   - Stored user email: ${storedUser?.email}")
        println("   - Stored user ID: ${storedUser?.id}")
        println("   - Stored token exists: ${storedToken != null}")
        println("   - Stored token preview: ${storedToken?.take(50)}...")
        println("   - Stored role: $storedRole")
    }
} 