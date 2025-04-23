package com.clockwise.features.auth.data.repository

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
        username: String, 
        email: String, 
        password: String
    ): Flow<Result<AuthResponse, DataError.Remote>> = flow {
        val result = remoteDataSource.register(username, email, password)
        
        if (result is Result.Success) {
            userService.saveAuthResponse(result.data)
        }
        
        emit(result)
    }

    override suspend fun login(
        email: String, 
        password: String
    ): Flow<Result<AuthResponse, DataError.Remote>> = flow {
        val result = remoteDataSource.login(email, password)
        
        if (result is Result.Success) {
            userService.saveAuthResponse(result.data)
        }
        
        emit(result)
    }

    override suspend fun logout(): Flow<Result<Unit, DataError.Remote>> = flow {
        userService.clearAuthData()
        emit(Result.Success(Unit))
    }
} 