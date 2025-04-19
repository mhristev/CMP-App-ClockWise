package com.clockwise.user.data.network

import com.clockwise.core.di.ApiConfig
import com.clockwise.user.data.model.AuthResponse
import com.clockwise.user.data.model.UserDto
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.plcoding.bookpedia.core.domain.Result

class KtorRemoteUserDataSource(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
): RemoteUserDataSource {
    override suspend fun register(username: String, email: String, password: String): Result<AuthResponse, DataError.Remote> {
        return safeCall<AuthResponse> {
            httpClient.post("${apiConfig.baseAuthUrl}/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequestDto(username, email, password))
            }
        }
    }

    override suspend fun login(email: String, password: String): Result<AuthResponse, DataError.Remote> {
        return safeCall<AuthResponse> {
            httpClient.post("${apiConfig.baseAuthUrl}/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDto(email, password))
            }
        }
    }
}

@Serializable
data class RegisterRequestDto(
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
)

@Serializable
data class LoginRequestDto(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String
)

@Serializable
data class RegisterResponseDto(
    val id: String,
    val username: String,
    val email: String,
    val role: String,
    val restaurantId: String
)

@Serializable
class LoginResponseDto (
    val token: String
)
