package com.clockwise.user.presentation.user_auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import co.touchlab.skie.configuration.annotations.FlowInterop
import com.clockwise.user.data.network.RemoteUserDataSource
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update


class AuthViewModel(private val remoteUserDataSource: RemoteUserDataSource) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    

    val state: StateFlow<AuthState> = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    init {
        // Initialize state
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    isAuthenticated = false,
                    resultMessage = null
                )
            }
        }
    }

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.LoadInitialState -> {
                viewModelScope.launch {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isAuthenticated = false,
                            resultMessage = null
                        )
                    }
                }
            }
            is AuthAction.OnRegister -> register(action.email, action.username, action.password, action.confirmPassword)
            is AuthAction.OnLogin -> login(action.email, action.password)
        }
    }

    private fun register(email: String, username: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            if (password != confirmPassword) {
                _state.update {
                    it.copy(
                        resultMessage = "Passwords do not match",
                        isLoading = false
                    )
                }
            } else {
                if (email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                    remoteUserDataSource.register(
                        username,
                        email,
                        password,
                        "123"
                    )
                        .onError {
                            _state.update { state ->
                                state.copy(
                                    resultMessage = it.toString(),
                                    isLoading = false,
                                    isAuthenticated = false
                                )
                            }
                        }
                        .onSuccess {
                            _state.update { state ->
                                state.copy(
                                    resultMessage = it.toString(),
                                    isLoading = false,
                                    isAuthenticated = true
                                )
                            }
                        }
                } else {
                    _state.update { state ->
                        state.copy(
                            resultMessage = "Please fill out all fields",
                            isLoading = false,
                            isAuthenticated = false
                        )
                    }
                }
            }
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            if (email.isNotEmpty() && password.isNotEmpty()) {
                remoteUserDataSource.login(email, password)
                    .onError {
                        _state.update { state ->
                            state.copy(
                                resultMessage = it.toString(),
                                isLoading = false,
                                isAuthenticated = false
                            )
                        }
                    }
                    .onSuccess {
                        _state.update { state ->
                            state.copy(
                                resultMessage = it.toString(),
                                isLoading = false,
                                isAuthenticated = true
                            )
                        }
                    }
            } else {
                _state.update { state ->
                    state.copy(
                        resultMessage = "Please fill out all fields",
                        isLoading = false,
                        isAuthenticated = false
                    )
                }
            }
        }
    }
}