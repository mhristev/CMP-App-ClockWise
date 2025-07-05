package com.clockwise.features.availability.domain.network

import com.clockwise.features.auth.UserService
import com.clockwise.core.di.ApiConfig
import com.clockwise.core.TimeProvider
import com.clockwise.features.availability.data.dto.AvailabilityDto
import com.clockwise.features.availability.data.dto.AvailabilityRequest
import com.clockwise.features.availability.data.network.RemoteAvailabilityDataSource
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.json.Json

class KtorRemoteAvailabilityDataSource(
    private val httpClient: HttpClient,
    private val userService: com.clockwise.features.auth.UserService,
    private val apiConfig: ApiConfig
) : RemoteAvailabilityDataSource {
    
    override suspend fun getUserAvailabilities(): Result<List<AvailabilityDto>, DataError.Remote> {
        val currentUserId = userService.currentUser.value?.id
            ?: return Result.Error(DataError.Remote.UNKNOWN)
        
        val token = userService.authToken.value
            ?: return Result.Error(DataError.Remote.UNKNOWN)
            
        return safeCall {
            httpClient.get("${apiConfig.baseAvailabilityUrl}/users/me/availabilities") {
                parameter("userId", currentUserId)
                header("Authorization", "Bearer $token")
            }
        }
    }
    
    override suspend fun createAvailability(
        date: LocalDate, 
        startTimeString: String, 
        endTimeString: String
    ): Result<AvailabilityDto, DataError.Remote> {
        val currentUserId = userService.currentUser.value?.id
            ?: return Result.Error(DataError.Remote.UNKNOWN)
            
        val token = userService.authToken.value
            ?: return Result.Error(DataError.Remote.UNKNOWN)
            
        val businessUnitId = userService.currentUser.value?.businessUnitId
        
        // Parse the time strings (HH:mm format)
        val startComponents = startTimeString.split(":").map { it.toInt() }
        val endComponents = endTimeString.split(":").map { it.toInt() }
        
        if (startComponents.size != 2 || endComponents.size != 2) {
            return Result.Error(DataError.Remote.UNKNOWN)
        }
        
        // Create LocalDateTime objects for start and end times
        val startDateTime = LocalDateTime(
            date.year, date.monthNumber, date.dayOfMonth,
            startComponents[0], startComponents[1], 0, 0
        )
        val endDateTime = LocalDateTime(
            date.year, date.monthNumber, date.dayOfMonth,
            endComponents[0], endComponents[1], 0, 0
        )
        
        // Convert to ISO-8601 format with timezone for API request
        val startTimeIso = TimeProvider.formatIsoDateTime(startDateTime)
        val endTimeIso = TimeProvider.formatIsoDateTime(endDateTime)
        
        val request = AvailabilityRequest(
            employeeId = currentUserId,
            startTime = startTimeIso,
            endTime = endTimeIso,
            businessUnitId = businessUnitId
        )
        
        return safeCall {
            httpClient.post("${apiConfig.baseAvailabilityUrl}/availabilities") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(AvailabilityRequest.serializer(), request))
                header("Authorization", "Bearer $token")
            }
        }
    }
    
    override suspend fun updateAvailability(
        id: String,
        date: LocalDate, 
        startTimeString: String, 
        endTimeString: String
    ): Result<AvailabilityDto, DataError.Remote> {
        val currentUserId = userService.currentUser.value?.id
            ?: return Result.Error(DataError.Remote.UNKNOWN)
            
        val token = userService.authToken.value
            ?: return Result.Error(DataError.Remote.UNKNOWN)
            
        val businessUnitId = userService.currentUser.value?.businessUnitId
        
        // Parse the time strings (HH:mm format)
        val startComponents = startTimeString.split(":").map { it.toInt() }
        val endComponents = endTimeString.split(":").map { it.toInt() }
        
        if (startComponents.size != 2 || endComponents.size != 2) {
            return Result.Error(DataError.Remote.UNKNOWN)
        }
        
        // Create LocalDateTime objects for start and end times
        val startDateTime = LocalDateTime(
            date.year, date.monthNumber, date.dayOfMonth,
            startComponents[0], startComponents[1], 0, 0
        )
        val endDateTime = LocalDateTime(
            date.year, date.monthNumber, date.dayOfMonth,
            endComponents[0], endComponents[1], 0, 0
        )
        
        // Convert to ISO-8601 format with timezone for API request
        val startTimeIso = TimeProvider.formatIsoDateTime(startDateTime)
        val endTimeIso = TimeProvider.formatIsoDateTime(endDateTime)
        
        val request = AvailabilityRequest(
            employeeId = currentUserId,
            startTime = startTimeIso,
            endTime = endTimeIso,
            businessUnitId = businessUnitId
        )
        
        return safeCall {
            httpClient.put("${apiConfig.baseAvailabilityUrl}/availabilities/$id") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(AvailabilityRequest.serializer(), request))
                header("Authorization", "Bearer $token")
            }
        }
    }
    
    override suspend fun deleteAvailability(id: String): Result<Boolean, DataError.Remote> {
        val token = userService.authToken.value
            ?: return Result.Error(DataError.Remote.UNKNOWN)
            
        return safeCall {
            httpClient.delete("${apiConfig.baseAvailabilityUrl}/availabilities/$id") {
                header("Authorization", "Bearer $token")
            }
        }
    }
}