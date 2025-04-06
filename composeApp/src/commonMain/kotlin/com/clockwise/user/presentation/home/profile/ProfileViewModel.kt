package com.clockwise.user.presentation.home.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.service.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userService: UserService
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.LoadUserProfile -> loadUserProfile()
            is ProfileAction.UpdateProfile -> updateProfile(action.profile)
            is ProfileAction.Logout -> logout()
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val currentUser = userService.currentUser.value
            if (currentUser != null) {
                _state.update { state ->
                    state.copy(
                        userProfile = UserProfile(
                            name = currentUser.username,
                            email = currentUser.email,
                            role = currentUser.role.name,
                            company = currentUser.businessUnitName ?: "Not assigned",
                            phone = null
                        ),
                        isLoading = false
                    )
                }
            } else {
                _state.update { state ->
                    state.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    userProfile = profile
                )
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userService.clearAuthData()
        }
    }
} 