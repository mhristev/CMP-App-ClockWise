package com.clockwise.features.business.data.repository

import com.clockwise.features.business.presentation.add_employee.User
import com.plcoding.bookpedia.core.domain.DataError
import kotlinx.coroutines.flow.Flow
import com.plcoding.bookpedia.core.domain.Result

interface SearchRepository {
    suspend fun searchUsers(query: String): Flow<Result<List<User>, DataError.Remote>>
    
    suspend fun addUserToBusinessUnit(userId: String, businessUnitId: String): Flow<Result<Unit, DataError.Remote>>
} 