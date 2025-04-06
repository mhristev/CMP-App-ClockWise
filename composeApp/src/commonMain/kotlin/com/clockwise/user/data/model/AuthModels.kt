package com.clockwise.user.data.model

import com.clockwise.user.domain.UserRole
import kotlinx.serialization.SerialName
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