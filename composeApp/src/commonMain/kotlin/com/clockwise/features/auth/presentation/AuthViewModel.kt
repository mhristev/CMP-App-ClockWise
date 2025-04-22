package com.clockwise.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import co.touchlab.skie.configuration.annotations.FlowInterop
import com.clockwise.core.UserService
import com.clockwise.features.auth.data.network.RemoteUserDataSource
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class AuthViewModel(
    private val userService: UserService,
    private val remoteUserDataSource: RemoteUserDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        // Initialize state
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = false,
                isAuthenticated = false,
                resultMessage = null,
                hasBusinessUnit = false
            )
        }
    }

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.Login -> login(action.email, action.password)
            is AuthAction.Register -> register(action.email, action.username, action.password, action.confirmPassword)
            is AuthAction.Logout -> logout()
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, resultMessage = null)
            
            if (email.isBlank() || password.isBlank()) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    resultMessage = "Please fill in all fields"
                )
                return@launch
            }

            remoteUserDataSource.login(email, password)
                .onSuccess { response ->
                    userService.saveAuthResponse(response)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        hasBusinessUnit = response.user.businessUnitId != null,
                        resultMessage = "Login successful"
                    )
                }
                .onError { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        resultMessage = "Login failed:"
                    )
                }
        }
    }

    private fun register(email: String, username: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, resultMessage = null)
            
            if (email.isBlank() || username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    resultMessage = "Please fill in all fields"
                )
                return@launch
            }

            if (password != confirmPassword) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    resultMessage = "Passwords do not match"
                )
                return@launch
            }

            remoteUserDataSource.register(email, username, password)
                .onSuccess { response ->
                    userService.saveAuthResponse(response)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        hasBusinessUnit = response.user.businessUnitId != null,
                        resultMessage = "Registration successful"
                    )
                }
                .onError { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        resultMessage = "Registration failed:"
                    )
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userService.clearAuthData()
            _state.value = AuthState()
        }
    }
}

sealed class AuthAction {
    data class Login(val email: String, val password: String) : AuthAction()
    data class Register(
        val email: String,
        val username: String,
        val password: String,
        val confirmPassword: String
    ) : AuthAction()
    object Logout : AuthAction()
}

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val hasBusinessUnit: Boolean = false,
    val resultMessage: String? = null
)