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
        if (currentUserId == null) {
            println("DEBUG: getUserAvailabilities - No current user ID found")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
        
        val token = userService.getValidAuthToken()
        if (token == null) {
            println("DEBUG: getUserAvailabilities - No valid auth token found")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
        
        println("DEBUG: getUserAvailabilities - User ID: $currentUserId")
        println("DEBUG: getUserAvailabilities - Token available: ${token.isNotEmpty()}")
        println("DEBUG: getUserAvailabilities - Token length: ${token.length}")
        println("DEBUG: getUserAvailabilities - Token preview: ${token.take(10)}...")
        println("DEBUG: getUserAvailabilities - User authorized: ${userService.isUserAuthorized}")
        
        val url = "${apiConfig.baseAvailabilityUrl}/users/${currentUserId}/availabilities"
        println("DEBUG: getUserAvailabilities - URL: $url")
            
        return safeCall {
            httpClient.get(url) {
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
        if (currentUserId == null) {
            println("DEBUG: createAvailability - No current user ID found")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
            
        val token = userService.getValidAuthToken()
        if (token == null) {
            println("DEBUG: createAvailability - No valid auth token found")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
        
        println("DEBUG: createAvailability - User ID: $currentUserId")
        println("DEBUG: createAvailability - Token available: ${token.isNotEmpty()}")
        println("DEBUG: createAvailability - Token length: ${token.length}")
        println("DEBUG: createAvailability - Token preview: ${token.take(10)}...")
        println("DEBUG: createAvailability - User authorized: ${userService.isUserAuthorized}")
            
        val businessUnitId = userService.currentUser.value?.businessUnitId
        
        // Clean and validate time strings - remove any extra quotes or whitespace
        val cleanStartTime = startTimeString.trim().replace("\"", "")
        val cleanEndTime = endTimeString.trim().replace("\"", "")
        
        // Parse the time strings (HH:mm format) with better error handling
        val startComponents = try {
            cleanStartTime.split(":").map { component ->
                component.trim().toIntOrNull() ?: throw NumberFormatException("Invalid time component: $component")
            }
        } catch (e: Exception) {
            println("Error parsing start time '$startTimeString': ${e.message}")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
        
        val endComponents = try {
            cleanEndTime.split(":").map { component ->
                component.trim().toIntOrNull() ?: throw NumberFormatException("Invalid time component: $component")
            }
        } catch (e: Exception) {
            println("Error parsing end time '$endTimeString': ${e.message}")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
        
        if (startComponents.size != 2 || endComponents.size != 2) {
            println("Invalid time format. Expected HH:mm, got start: '$cleanStartTime', end: '$cleanEndTime'")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
        
        // Validate time values
        val startHour = startComponents[0]
        val startMinute = startComponents[1]
        val endHour = endComponents[0]
        val endMinute = endComponents[1]
        
        if (startHour !in 0..23 || startMinute !in 0..59 || endHour !in 0..23 || endMinute !in 0..59) {
            println("Invalid time values. Hours must be 0-23, minutes 0-59")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
        
        // Create LocalDateTime objects for start and end times
        val startDateTime = LocalDateTime(
            date.year, date.monthNumber, date.dayOfMonth,
            startHour, startMinute, 0, 0
        )
        val endDateTime = LocalDateTime(
            date.year, date.monthNumber, date.dayOfMonth,
            endHour, endMinute, 0, 0
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
            
        val token = userService.getValidAuthToken()
            ?: return Result.Error(DataError.Remote.UNKNOWN)
            
        val businessUnitId = userService.currentUser.value?.businessUnitId
        
        // Clean and validate time strings - remove any extra quotes or whitespace
        val cleanStartTime = startTimeString.trim().replace("\"", "")
        val cleanEndTime = endTimeString.trim().replace("\"", "")
        
        // Parse the time strings (HH:mm format) with better error handling
        val startComponents = try {
            cleanStartTime.split(":").map { component ->
                component.trim().toIntOrNull() ?: throw NumberFormatException("Invalid time component: $component")
            }
        } catch (e: Exception) {
            println("Error parsing start time '$startTimeString': ${e.message}")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
        
        val endComponents = try {
            cleanEndTime.split(":").map { component ->
                component.trim().toIntOrNull() ?: throw NumberFormatException("Invalid time component: $component")
            }
        } catch (e: Exception) {
            println("Error parsing end time '$endTimeString': ${e.message}")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
        
        if (startComponents.size != 2 || endComponents.size != 2) {
            println("Invalid time format. Expected HH:mm, got start: '$cleanStartTime', end: '$cleanEndTime'")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
        
        // Validate time values
        val startHour = startComponents[0]
        val startMinute = startComponents[1]
        val endHour = endComponents[0]
        val endMinute = endComponents[1]
        
        if (startHour !in 0..23 || startMinute !in 0..59 || endHour !in 0..23 || endMinute !in 0..59) {
            println("Invalid time values. Hours must be 0-23, minutes 0-59")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
        
        // Create LocalDateTime objects for start and end times
        val startDateTime = LocalDateTime(
            date.year, date.monthNumber, date.dayOfMonth,
            startHour, startMinute, 0, 0
        )
        val endDateTime = LocalDateTime(
            date.year, date.monthNumber, date.dayOfMonth,
            endHour, endMinute, 0, 0
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
        val token = userService.getValidAuthToken()
            ?: return Result.Error(DataError.Remote.UNKNOWN)
            
        return try {
            val response = httpClient.delete("${apiConfig.baseAvailabilityUrl}/availabilities/$id") {
                header("Authorization", "Bearer $token")
            }
            
            // Check if the response status indicates success (204 No Content)
            if (response.status.value in 200..299) {
                Result.Success(true)
            } else {
                Result.Error(DataError.Remote.SERVER)
            }
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
}