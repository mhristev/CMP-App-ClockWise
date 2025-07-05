package com.clockwise.features.profile.domain.repository

import com.clockwise.core.model.User
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    suspend fun getUserProfile(): Result<User, DataError.Remote>
}