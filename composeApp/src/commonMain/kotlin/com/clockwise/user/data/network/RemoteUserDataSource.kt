package com.clockwise.user.data.network

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface RemoteUserDataSource {
    suspend fun register(username: String, email: String, password: String, restaurantId: String): Result<RegisterResponseDto, DataError.Remote>
    suspend fun login(username: String, password: String): Result<LoginResponseDto, DataError.Remote>
}