package com.clockwise.features.auth.domain.repository

import com.clockwise.core.model.PrivacyConsent
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
    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        privacyConsent: PrivacyConsent
    ): Result<AuthResponse, DataError.Remote>
    
    /**
     * Login existing user
     */
    suspend fun login(email: String, password: String): Result<AuthResponse, DataError.Remote>
    
    /**
     * Logout current user
     */
    suspend fun logout(): Result<Unit, DataError.Remote>
} 