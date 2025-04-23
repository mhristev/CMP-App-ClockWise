package com.clockwise.core

import com.clockwise.core.data.SecureStorage
import com.clockwise.core.model.User
import com.clockwise.core.model.UserRole
import com.clockwise.features.auth.UserService as AuthUserService
import com.clockwise.features.auth.data.local.UserDto
import com.clockwise.features.auth.domain.model.AuthResponse
import kotlinx.coroutines.flow.StateFlow

/**
 * Core UserService that delegates to the auth feature's UserService
 * This allows gradual migration from the old implementation to the new one
 */
class UserService(secureStorage: SecureStorage) {
    
    private val authUserService = AuthUserService(secureStorage)
    
    val currentUser: StateFlow<User?> = authUserService.currentUser
    val authToken: StateFlow<String?> = authUserService.authToken
    
    fun saveAuthResponse(response: AuthResponse) {
        authUserService.saveAuthResponse(response)
    }
    
    fun clearAuthData() {
        authUserService.clearAuthData()
    }
    
    fun isUserAuthorized(): Boolean {
        return authUserService.isUserAuthorized()
    }
    
    fun hasManagerAccess(): Boolean {
        return authUserService.hasManagerAccess()
    }
    
    fun getCurrentUserBusinessUnitId(): String? {
        return authUserService.getCurrentUserBusinessUnitId()
    }
} 