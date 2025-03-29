package com.clockwise.user.presentation.user_auth

sealed interface AuthAction {
    object LoadInitialState : AuthAction
    data class OnRegister(
        val email: String,
        val username: String,
        val password: String,
        val confirmPassword: String
    ): AuthAction
    data class OnLogin(val email: String, val password: String): AuthAction
}