package com.clockwise.user.data.network

import com.clockwise.user.data.model.AuthResponse
import com.clockwise.user.data.model.UserDto
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface RemoteUserDataSource {
    suspend fun register(username: String, email: String, password: String): Result<AuthResponse, DataError.Remote>
    suspend fun login(email: String, password: String): Result<AuthResponse, DataError.Remote>
}