package com.clockwise.features.profile.data.repository

import com.clockwise.features.profile.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for profile-related operations
 */
interface ProfileRepository {
    /**
     * Get the current user's profile information
     */
    suspend fun getUserProfile(): UserProfile?
    
    /**
     * Update the user's profile information
     */
    suspend fun updateUserProfile(profile: UserProfile): Result<UserProfile>
    
    /**
     * Log the user out and clear authentication data
     */
    suspend fun logout(): Result<Unit>
    
    /**
     * Anonymize the current user's account as per GDPR requirements
     */
    suspend fun anonymizeUserAccount(): Result<Unit>
} 