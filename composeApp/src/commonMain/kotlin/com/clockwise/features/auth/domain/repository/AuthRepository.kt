package com.clockwise.features.auth.domain.repository

import com.clockwise.features.auth.domain.model.AuthResponse
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication-related operations
 */
interface AuthRepository {
    /**
     * Register a new user
     */
    suspend fun register(username: String, email: String, password: String): Flow<Result<AuthResponse, DataError.Remote>>
    
    /**
     * Login existing user
     */
    suspend fun login(email: String, password: String): Flow<Result<AuthResponse, DataError.Remote>>
    
    /**
     * Logout current user
     */
    suspend fun logout(): Flow<Result<Unit, DataError.Remote>>
} 