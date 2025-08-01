package com.clockwise.features.business.data.repository

import com.clockwise.core.model.User
import com.plcoding.bookpedia.core.domain.DataError
import kotlinx.coroutines.flow.Flow
import com.plcoding.bookpedia.core.domain.Result

interface UserRepository {
    suspend fun searchUsers(query: String): Flow<Result<List<User>, DataError.Remote>>
    
    suspend fun addUserToBusinessUnit(userId: String, businessUnitId: String): Flow<Result<Unit, DataError.Remote>>
} 