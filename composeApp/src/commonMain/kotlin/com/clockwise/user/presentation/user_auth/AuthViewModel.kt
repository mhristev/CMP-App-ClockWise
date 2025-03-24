package com.clockwise.user.presentation.user_auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.OnRegister -> register(action.email, action.password, action.confirmPassword)
            is AuthAction.OnLogin -> login(action.email, action.password)
        }
    }

    private fun register(username: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            setIsLoading(true)
            if (password != confirmPassword) {
                _state.update {
                    it.copy(
                        resultMessage = "Passwords do not match"
                    )
                }
            } else {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    remoteUserDataSource.register(
                        username,
                        ("$username@mail.com"),
                        password,
                        "123"
                    )
                        .onError {
                            setResultMessage(it.toString())
                        }
                        .onSuccess {
                            setResultMessage(it.toString())
                        }
                } else {
                    setResultMessage("Please fill out all fields")
                }
            }
            setIsLoading(false)
        }
    }

    private fun login(username: String, password: String) {
        viewModelScope.launch {
            remoteUserDataSource.login(username, password)
                .onError {
                    setResultMessage(it.toString())
                }
                .onSuccess {
                    setIsAuthenticated(true)
                    setResultMessage(it.toString())
                }
        }
    }

    private fun setResultMessage(msg: String) {
        _state.update {
            it.copy(
                resultMessage = msg
            )
        }
    }
    private fun setIsAuthenticated(isAuthenticated: Boolean) {
        _state.update {
            it.copy(
                isAuthenticated = isAuthenticated
            )
        }
    }

    private fun setIsLoading(isLoading: Boolean) {
        _state.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }
}