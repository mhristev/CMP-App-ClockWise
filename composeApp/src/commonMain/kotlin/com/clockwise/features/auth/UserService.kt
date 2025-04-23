package com.clockwise.features.auth

import com.clockwise.core.data.SecureStorage
import com.clockwise.core.model.User
import com.clockwise.core.model.UserRole
import com.clockwise.features.auth.data.local.UserDto
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
    private val _currentUser = MutableStateFlow<User?>(secureStorage.getUser())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _authToken = MutableStateFlow<String?>(secureStorage.getAuthToken())
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

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
        _currentUser.value = response.user
        _authToken.value = response.token
    }

    /**
     * Clear authentication data from secure storage and state flows
     */
    fun clearAuthData() {
        secureStorage.clearAuthData()
        _currentUser.value = null
        _authToken.value = null
    }

    /**
     * Check if user is currently authorized
     */
    fun isUserAuthorized(): Boolean {
        return _currentUser.value != null && _authToken.value != null && !secureStorage.isTokenExpired()
    }

    /**
     * Check if current user has manager-level access
     */
    fun hasManagerAccess(): Boolean {
        val role = _currentUser.value?.role
        return role == UserRole.MANAGER || role == UserRole.ADMIN
    }

    /**
     * Get the business unit ID for the current user
     */
    fun getCurrentUserBusinessUnitId(): String? {
        return _currentUser.value?.businessUnitId
    }
} 