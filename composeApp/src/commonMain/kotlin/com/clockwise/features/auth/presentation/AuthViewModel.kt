package com.clockwise.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import co.touchlab.skie.configuration.annotations.FlowInterop
import com.clockwise.core.model.PrivacyConsent
import com.clockwise.features.auth.UserService
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
                isAuthenticated = userService.isUserAuthorized(),
                resultMessage = null,
                hasBusinessUnit = userService.getCurrentUserBusinessUnitId() != null
            )
        }
    }

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.Login -> login(action.email, action.password)
            is AuthAction.Register -> register(
                action.email,
                action.password,
                action.confirmPassword,
                action.firstName,
                action.lastName,
                action.phoneNumber,
                action.privacyConsent
            )
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
                        resultMessage = "Login failed: ${error.name}"
                    )
                }
        }
    }

    private fun register(
        email: String,
        password: String,
        confirmPassword: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        privacyConsent: PrivacyConsent
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, resultMessage = null)
            
            if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() || 
                firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    resultMessage = "Please fill in all required fields"
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
            
            // Validate that at least GDPR consent is provided
            if (!privacyConsent.thirdPartyDataSharingConsent) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    resultMessage = "You must accept the privacy policy to register"
                )
                return@launch
            }

            remoteUserDataSource.register(
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                privacyConsent = privacyConsent
            )
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
                        resultMessage = "Registration failed: ${error.name}"
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
        val password: String,
        val confirmPassword: String,
        val firstName: String,
        val lastName: String,
        val phoneNumber: String,
        val privacyConsent: PrivacyConsent
    ) : AuthAction()
    object Logout : AuthAction()
}

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val hasBusinessUnit: Boolean = false,
    val resultMessage: String? = null
)