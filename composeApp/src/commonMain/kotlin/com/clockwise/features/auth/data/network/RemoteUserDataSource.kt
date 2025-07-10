package com.clockwise.features.auth.data.network

import com.clockwise.core.model.PrivacyConsent
import com.clockwise.features.auth.domain.model.AuthResponse
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface RemoteUserDataSource {
    suspend fun register(
        email: String, 
        password: String, 
        firstName: String, 
        lastName: String, 
        phoneNumber: String, 
        privacyConsent: PrivacyConsent
    ): Result<AuthResponse, DataError.Remote>
    
    suspend fun login(email: String, password: String): Result<AuthResponse, DataError.Remote>
    
    suspend fun refreshToken(refreshToken: String): Result<AuthResponse, DataError.Remote>
}