package com.clockwise.features.auth.data.network

import com.clockwise.core.di.ApiConfig
import com.clockwise.core.model.PrivacyConsent
import com.clockwise.features.auth.domain.model.AuthResponse
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

/**
 * Implementation of RemoteUserDataSource that uses Ktor HTTP client
 */
class KtorRemoteUserDataSource(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
): RemoteUserDataSource {
    override suspend fun register(
        email: String, 
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        privacyConsent: PrivacyConsent
    ): Result<AuthResponse, DataError.Remote> {
        return safeCall {
            httpClient.post("${apiConfig.baseAuthUrl}/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequestDto(
                    email = email,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    privacyConsent = privacyConsent
                ))
            }
        }
    }

    override suspend fun login(
        email: String, 
        password: String
    ): Result<AuthResponse, DataError.Remote> {
        return safeCall {
            httpClient.post("${apiConfig.baseAuthUrl}/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDto(email, password))
            }
        }
    }
}

/**
 * Data transfer object for registration request
 */
@Serializable
data class RegisterRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("phoneNumber") val phoneNumber: String,
    @SerialName("privacyConsent") val privacyConsent: PrivacyConsent
)

/**
 * Data transfer object for login request
 */
@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class RegisterResponseDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val role: String,
    val businessUnitId: String?
)

@Serializable
class LoginResponseDto (
    val token: String
)
