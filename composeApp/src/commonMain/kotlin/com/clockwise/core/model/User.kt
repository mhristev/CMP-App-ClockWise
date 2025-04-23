package com.clockwise.core.model

import kotlinx.serialization.Serializable

enum class UserRole {
    ADMIN,
    MANAGER,
    EMPLOYEE
}

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val role: UserRole,
    val businessUnitId: String?,
    val businessUnitName: String?
)
