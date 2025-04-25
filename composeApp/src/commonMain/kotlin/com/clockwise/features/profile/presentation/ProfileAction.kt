package com.clockwise.features.profile.presentation

import com.clockwise.features.profile.domain.model.UserProfile

/**
 * Actions that can be performed on the profile screen
 */
sealed class ProfileAction {
    /**
     * Load the user's profile information
     */
    data object LoadUserProfile : ProfileAction()
    
    /**
     * Update the user's profile information
     */
    data class UpdateProfile(val profile: UserProfile) : ProfileAction()
    
    /**
     * Log the user out
     */
    data object Logout : ProfileAction()
    
    /**
     * Anonymize the user's account (GDPR right to be forgotten)
     */
    data object AnonymizeAccount : ProfileAction()
} 