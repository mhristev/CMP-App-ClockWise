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
        userService.clearAuthData()
        return Result.Success(Unit)
    }
} 