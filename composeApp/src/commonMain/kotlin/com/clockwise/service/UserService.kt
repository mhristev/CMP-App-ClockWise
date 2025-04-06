package com.clockwise.service

import com.clockwise.user.data.model.AuthResponse
import com.clockwise.user.data.model.UserDto
import com.clockwise.user.domain.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserService {
    private val _currentUser = MutableStateFlow<UserDto?>(null)
    val currentUser: StateFlow<UserDto?> = _currentUser.asStateFlow()

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    fun saveAuthResponse(response: AuthResponse) {
        _currentUser.value = response.user
        _authToken.value = response.token
    }

    fun clearAuthData() {
        _currentUser.value = null
        _authToken.value = null
    }

    fun isUserAuthorized(): Boolean {
        return _currentUser.value != null && _authToken.value != null
    }

    fun hasManagerAccess(): Boolean {
        val role = _currentUser.value?.role
        return role == UserRole.MANAGER || role == UserRole.ADMIN
    }

    fun getCurrentUserBusinessUnitId(): String? {
        return _currentUser.value?.businessUnitId
    }
} 