package com.clockwise.user.presentation.user_auth

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val resultMessage: String? = null
)
