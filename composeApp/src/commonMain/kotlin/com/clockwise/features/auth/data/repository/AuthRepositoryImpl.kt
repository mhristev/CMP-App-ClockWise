package com.clockwise.features.auth.data.repository

import com.clockwise.core.model.PrivacyConsent
import com.clockwise.features.auth.UserService
import com.clockwise.features.auth.data.network.RemoteUserDataSource
import com.clockwise.features.auth.domain.model.AuthResponse
import com.clockwise.features.auth.domain.repository.AuthRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementation of the AuthRepository interface
 */
class AuthRepositoryImpl(
    private val remoteDataSource: RemoteUserDataSource,
    private val userService: UserService
) : AuthRepository {

    init {
        // Set up the refresh token function for UserService
        userService.setRefreshTokenFunction { refreshToken ->
            remoteDataSource.refreshToken(refreshToken)
        }
    }

    override suspend fun register(
        email: String, 
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        privacyConsent: PrivacyConsent
    ): Result<AuthResponse, DataError.Remote> {
        val result = remoteDataSource.register(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            privacyConsent = privacyConsent
        )
        
        if (result is Result.Success) {
            userService.saveAuthResponse(result.data)
        }
        
        return result
    }

    override suspend fun login(
        email: String, 
        password: String
    ): Result<AuthResponse, DataError.Remote> {
        val result = remoteDataSource.login(email, password)
        
        if (result is Result.Success) {
            userService.saveAuthResponse(result.data)
        }
        
        return result
    }

    override suspend fun logout(): Result<Unit, DataError.Remote> {
        // Use the comprehensive data clearing service through UserService
        userService.clearAllUserData()
        return Result.Success(Unit)
    }

    /**
     * Refresh the authentication token using the refresh token
     */
    suspend fun refreshToken(): Result<AuthResponse, DataError.Remote> {
        // UserService will handle this internally now
        val newToken = userService.getValidAuthToken()
        return if (newToken != null) {
            Result.Success(AuthResponse(
                accessToken = newToken,
                refreshToken = "", // Placeholder - actual refresh token is stored internally
                expiresIn = 3600,
                role = userService.currentUserRole.value?.name ?: "EMPLOYEE"
            ))
        } else {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
} 