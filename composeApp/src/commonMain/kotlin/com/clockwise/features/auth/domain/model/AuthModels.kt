package com.clockwise.features.auth.domain.model

import com.clockwise.core.model.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: String?,
    val username: String,
    val email: String,
    val role: UserRole,
    val businessUnitId: String?,
    val businessUnitName: String?
) 