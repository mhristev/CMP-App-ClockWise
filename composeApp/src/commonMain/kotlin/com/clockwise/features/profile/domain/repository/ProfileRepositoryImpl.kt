package com.clockwise.features.profile.domain.repository

import com.clockwise.core.UserService
import com.clockwise.features.profile.data.repository.ProfileRepository
import com.clockwise.features.profile.domain.model.UserProfile

/**
 * Implementation of the ProfileRepository that uses UserService for data operations
 */
class ProfileRepositoryImpl(
    private val userService: UserService
) : ProfileRepository {
    
    override suspend fun getUserProfile(): UserProfile? {
        val currentUser = userService.currentUser.value ?: return null
        
        return UserProfile(
            name = currentUser.username,
            email = currentUser.email,
            role = currentUser.role.name,
            company = currentUser.businessUnitName ?: "Not assigned",
            phone = null // Assuming phone is not available from the UserService
        )
    }
    
    override suspend fun updateUserProfile(profile: UserProfile): Result<UserProfile> {
        // For now, we'll just return the profile as if it was updated successfully
        // In a real implementation, you would call an API to update the profile
        return Result.success(profile)
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            userService.clearAuthData()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 