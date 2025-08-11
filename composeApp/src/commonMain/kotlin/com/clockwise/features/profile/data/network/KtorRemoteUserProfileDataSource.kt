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
import kotlinx.datetime.Clock

class KtorRemoteUserProfileDataSource(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig,
    private val userService: UserService
) : RemoteUserProfileDataSource {
    override suspend fun getUserProfile(): Result<User, DataError.Remote> {
        // Check if user is authenticated - let Auth plugin handle token automatically
        val token = userService.getValidAuthToken()
            ?: return Result.Error(DataError.Remote.UNKNOWN)

        val timestamp = Clock.System.now().toEpochMilliseconds()
        
        println("🌐 KtorRemoteUserProfileDataSource: Making fresh GET request to /me endpoint")
        println("🔑 UserService provided token: ${token.take(30)}... (length: ${token.length})")
        println("🔗 URL: ${apiConfig.baseUsersUrl}/me")
        println("⏰ Request timestamp: $timestamp")
        println("🔧 Auth plugin will automatically handle Bearer token (NO manual Authorization header)")

        val result = safeCall<User> {
            httpClient.get("${apiConfig.baseUsersUrl}/me") {
                // Don't manually set Authorization header - let Auth plugin handle it
                header(HttpHeaders.CacheControl, "no-cache, no-store, must-revalidate")
                header(HttpHeaders.Pragma, "no-cache")
                header(HttpHeaders.Expires, "0")
                header("X-Requested-With", "XMLHttpRequest")
                header("X-Timestamp", timestamp.toString())
                url {
                    parameters.append("_t", timestamp.toString()) // Add timestamp as query parameter for cache busting
                }
                println("📤 Request headers set: Cache-Control: no-cache, Timestamp: $timestamp")
            }
        }
        
        when (result) {
            is Result.Success -> {
                val user = result.data
                println("✅ /me endpoint response successful: user=${user.email}, id=${user.id}")
                println("🔍 User details: firstName=${user.firstName}, lastName=${user.lastName}")
                println("🏢 Business details: unitId=${user.businessUnitId}, unitName=${user.businessUnitName}")
            }
            is Result.Error -> {
                println("❌ /me endpoint response failed: error=${result.error}")
            }
        }
        
        return result
    }
}