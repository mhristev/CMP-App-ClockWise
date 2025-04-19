package com.clockwise.user.data.local

import com.clockwise.user.domain.UserRole
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

abstract class UserPreferences {
    abstract suspend fun saveAuthData(
        token: String,
        refreshToken: String,
        tokenType: String,
        expiresIn: Long,
        userId: String?,
        username: String,
        email: String,
        role: UserRole,
        businessUnitId: String?,
        businessUnitName: String?
    )

    abstract suspend fun getAuthData(): AuthData?

    abstract suspend fun clearAuthData()
    
    // Flow properties for UI reactivity can be added by platform implementations
}

data class AuthData(
    val token: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val user: UserDto
)

data class UserDto(
    val id: String?,
    val username: String,
    val email: String,
    val role: UserRole,
    val businessUnitId: String?,
    val businessUnitName: String?
)