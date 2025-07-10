package com.clockwise.features.auth.data.network

import com.clockwise.core.di.ApiConfig
import com.clockwise.core.model.PrivacyConsent
import com.clockwise.features.auth.domain.model.AuthResponse
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class KtorRemoteUserDataSource(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) : RemoteUserDataSource {
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
                setBody(
                    RegisterRequestDto(
                        email = email,
                        password = password,
                        firstName = firstName,
                        lastName = lastName,
                        phoneNumber = phoneNumber,
                        privacyConsent = privacyConsent
                    )
                )
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

    override suspend fun refreshToken(
        refreshToken: String
    ): Result<AuthResponse, DataError.Remote> {
        return safeCall {
            httpClient.post("${apiConfig.baseAuthUrl}/refresh") {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenRequestDto(refreshToken))
            }
        }
    }
}

@Serializable
data class RegisterRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("phoneNumber") val phoneNumber: String,
    @SerialName("privacyConsent") val privacyConsent: PrivacyConsent
)

@Serializable
data class RefreshTokenRequestDto(
    @SerialName("refreshToken") val refreshToken: String
)

@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)