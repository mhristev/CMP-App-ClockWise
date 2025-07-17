package com.clockwise.features.clockin.data.network

import com.clockwise.core.di.ApiConfig
import com.clockwise.features.clockin.domain.model.ClockInRequest
import com.clockwise.features.clockin.domain.model.ClockInResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

/**
 * Ktor implementation of RemoteClockInDataSource.
 */
class KtorRemoteClockInDataSource(
    private val httpClient: HttpClient,
    private val userService: com.clockwise.features.auth.UserService,
    private val apiConfig: ApiConfig
) : RemoteClockInDataSource {
    
    companion object {
        private const val CLOCK_IN_ENDPOINT = "/api/v1/clockin"
        private const val CLOCK_OUT_ENDPOINT = "/api/v1/clockout"
        private const val CLOCK_STATUS_ENDPOINT = "/api/v1/clockin/status"
    }
    
    override suspend fun clockIn(request: ClockInRequest): ClockInResponse {
        val token = userService.getValidAuthToken()
            ?: throw Exception("No access token available")
        
        val response = httpClient.post(CLOCK_IN_ENDPOINT) {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        
        return response.body<ClockInResponse>()
    }
    
    override suspend fun isUserClockedIn(userId: String): Boolean {
        val token = userService.getValidAuthToken()
            ?: throw Exception("No access token available")
        
        val response = httpClient.get("$CLOCK_STATUS_ENDPOINT/$userId") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        
        return response.body<Boolean>()
    }
    
    override suspend fun clockOut(userId: String): Boolean {
        val token = userService.getValidAuthToken()
            ?: throw Exception("No access token available")
        
        val response = httpClient.post(CLOCK_OUT_ENDPOINT) {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(mapOf("userId" to userId))
        }
        
        return response.body<Boolean>()
    }
}
