package com.clockwise.core.model


enum class UserRole {
    ADMIN,
    MANAGER,
    EMPLOYEE
}

data class User(
    val id: String? = null,
    val username: String,
    val email: String,
    val password: String,
    val role: UserRole,
    val restaurantId: String? = null
)