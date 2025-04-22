package com.clockwise.features.auth.data.network

import com.clockwise.features.auth.domain.model.AuthResponse
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface RemoteUserDataSource {
    suspend fun register(username: String, email: String, password: String): Result<AuthResponse, DataError.Remote>
    suspend fun login(email: String, password: String): Result<AuthResponse, DataError.Remote>
}