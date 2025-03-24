package com.clockwise.user.data.network

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

private const val BASE_URL = "http://10.0.2.2:8081/v1/auth"

class KtorRemoteUserDataSource(private val httpClient: HttpClient): RemoteUserDataSource {
    override suspend fun register(username: String, email: String, password: String, restaurantId: String): Result<RegisterResponseDto, DataError.Remote> {
        return safeCall<RegisterResponseDto> {
            httpClient.post("$BASE_URL/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequestDto(username, email, password, restaurantId))
            }
        }
    }

    override suspend fun login(username: String, password: String): Result<LoginResponseDto, DataError.Remote> {
        return safeCall<LoginResponseDto> {
            httpClient.post("$BASE_URL/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDto(username, password))
            }
        }
    }
}

@Serializable
data class RegisterRequestDto(
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("restaurantId") val restaurantId: String
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
