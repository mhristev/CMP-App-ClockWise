package com.clockwise.user.domain.repository

import com.clockwise.user.presentation.home.search.User
import com.plcoding.bookpedia.core.domain.DataError
import kotlinx.coroutines.flow.Flow
import com.plcoding.bookpedia.core.domain.Result

interface SearchRepository {
    suspend fun searchUsers(query: String): Flow<Result<List<User>, DataError.Remote>>
} 