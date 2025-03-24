package com.clockwise.user.presentation.user_auth

data class AuthState(
    val resultMessage: String? = null,

    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false

)
