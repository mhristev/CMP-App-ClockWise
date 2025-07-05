package com.clockwise.features.auth

import com.clockwise.core.data.SecureStorage
import com.clockwise.core.model.User
import com.clockwise.core.model.UserRole
import com.clockwise.features.auth.domain.model.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Service for managing user authentication state and data
 * Uses secure storage to persist auth data between app launches
 */
class UserService(
    private val secureStorage: SecureStorage
) {
    // State flows for reactive UI updates
    private val _authToken = MutableStateFlow<String?>(secureStorage.getAuthToken())
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(secureStorage.getUser())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _currentUserRole = MutableStateFlow<UserRole?>(secureStorage.getUserRole())
    val currentUserRole: StateFlow<UserRole?> = _currentUserRole.asStateFlow()

    init {
        // Check if token is expired and clear if necessary
        if (secureStorage.isTokenExpired()) {
            clearAuthData()
        }
    }

    /**
     * Save authentication response to secure storage and update state flows
     */
    fun saveAuthResponse(response: AuthResponse) {
        secureStorage.saveAuthData(response)
        _authToken.value = secureStorage.getAuthToken()
        _currentUserRole.value = secureStorage.getUserRole()
    }

    /**
     * Save full User object to secure storage
     */
    fun saveUser(user: User) {
        secureStorage.saveUser(user)
        _currentUser.value = user
    }

    /**
     * Clear authentication data from secure storage and state flows
     */
    fun clearAuthData() {
        secureStorage.clearAuthData()
        _authToken.value = null
        _currentUser.value = null
        _currentUserRole.value = null
    }

    /**
     * Check if user is currently authorized
     */
    fun isUserAuthorized(): Boolean {
        return _authToken.value != null && _currentUserRole.value != null && !secureStorage.isTokenExpired()
    }

    /**
     * Check if current user has manager-level access
     */
    fun hasManagerAccess(): Boolean {
        val role = _currentUserRole.value
        return role == UserRole.MANAGER || role == UserRole.ADMIN
    }
}