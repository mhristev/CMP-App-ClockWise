package com.clockwise.features.profile.data.network

import com.clockwise.core.di.ApiConfig
import com.clockwise.core.model.User
import com.clockwise.features.auth.UserService
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

class KtorRemoteUserProfileDataSource(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig,
    private val userService: UserService
) : RemoteUserProfileDataSource {
    override suspend fun getUserProfile(): Result<User, DataError.Remote> {
        val token = userService.authToken.value
            ?: return Result.Error(DataError.Remote.UNKNOWN)

        return safeCall {
            
            httpClient.get("${apiConfig.baseUsersUrl}/me") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
}