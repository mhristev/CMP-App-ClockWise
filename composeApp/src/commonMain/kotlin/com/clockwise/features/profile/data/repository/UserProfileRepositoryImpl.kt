package com.clockwise.features.profile.data.repository

import com.clockwise.core.model.User
import com.clockwise.features.profile.data.network.RemoteUserProfileDataSource
import com.clockwise.features.profile.domain.repository.UserProfileRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserProfileRepositoryImpl(
    private val remoteDataSource: RemoteUserProfileDataSource
) : UserProfileRepository {
    override suspend fun getUserProfile(): Result<User, DataError.Remote> {

            val result = remoteDataSource.getUserProfile()
            return when (result) {
                is Result.Success -> {
                    Result.Success(result.data)
                }
                is Result.Error -> {
                    Result.Error(result.error)
                }
            }
    }
}