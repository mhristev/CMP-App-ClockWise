package com.clockwise.features.profile.domain.repository

import com.clockwise.features.auth.UserService
import com.clockwise.core.di.ApiConfig
import com.clockwise.features.profile.data.repository.ProfileRepository
import com.clockwise.features.profile.domain.model.UserProfile
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Implementation of the ProfileRepository that uses UserService for data operations
 */
class ProfileRepositoryImpl(
    private val userService: UserService,
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) : ProfileRepository {
    
    override suspend fun getUserProfile(): UserProfile? {
        val currentUser = userService.currentUser.value
        
        if (currentUser == null) {
            return null
        }
        
        return UserProfile(
            firstName = currentUser.firstName,
            lastName = currentUser.lastName,
            email = currentUser.email,
            role = currentUser.role.name,
            company = currentUser.businessUnitName ?: "Not assigned",
            phoneNumber = currentUser.phoneNumber
        )
    }
    
    override suspend fun updateUserProfile(profile: UserProfile): Result<UserProfile> {
        // For now, we'll just return the profile as if it was updated successfully
        // In a real implementation, you would call an API to update the profile
        return Result.success(profile)
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            userService.clearAllUserData()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun anonymizeUserAccount(): Result<Unit> {
        return try {
            val token = userService.getValidAuthToken() ?: throw IllegalStateException("No auth token available")
            
            httpClient.delete("${apiConfig.gdprUrl}/erase-me") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            
            // After successful anonymization, log the user out with comprehensive cleanup
            userService.clearAllUserData()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}