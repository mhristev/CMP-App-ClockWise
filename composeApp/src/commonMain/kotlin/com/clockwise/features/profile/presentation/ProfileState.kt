package com.clockwise.features.profile.presentation

import com.clockwise.features.profile.domain.model.UserProfile

/**
 * State for the profile screen
 */
data class ProfileState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAnonymizeConfirmation: Boolean = false,
    val redirectToAuth: Boolean = false
) 