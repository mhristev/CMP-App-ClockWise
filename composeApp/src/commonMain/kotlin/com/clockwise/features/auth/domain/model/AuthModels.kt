package com.clockwise.features.auth.domain.model

import com.clockwise.core.model.User
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val role: String
)