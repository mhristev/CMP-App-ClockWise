package com.clockwise.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.features.profile.data.repository.ProfileRepository
import com.clockwise.features.profile.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the profile screen
 */
class ProfileViewModel(
    private val repository: ProfileRepository
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

            try {
                val profile = repository.getUserProfile()
                _state.update { state ->
                    state.copy(
                        userProfile = profile,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        isLoading = false,
                        error = "Failed to load profile: ${e.message}"
                    )
                }
            }
        }
    }

    private fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            repository.updateUserProfile(profile).fold(
                onSuccess = { updatedProfile ->
                    _state.update { state ->
                        state.copy(
                            userProfile = updatedProfile,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            error = "Failed to update profile: ${error.message}"
                        )
                    }
                }
            )
        }
    }

    private fun logout() {
        viewModelScope.launch {
            repository.logout()
            // Navigation is handled by the UI
        }
    }
} 