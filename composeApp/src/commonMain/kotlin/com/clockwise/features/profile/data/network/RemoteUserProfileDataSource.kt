package com.clockwise.features.profile.data.network

import com.clockwise.core.model.User
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface RemoteUserProfileDataSource {
    suspend fun getUserProfile(): Result<User, DataError.Remote>
}