package com.clockwise.service

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class AvailabilityDto(
    val id: String? = null,
    val employeeId: String,
    val startTime: List<Int>, // [year, month, day, hour, minute]
    val endTime: List<Int>,   // [year, month, day, hour, minute]
    val createdAt: List<Int>? = null, // [year, month, day, hour, minute, second, nano]
    val updatedAt: List<Int>? = null  // [year, month, day, hour, minute, second, nano]
)

@Serializable
data class AvailabilityRequest(
    val employeeId: String,
    val startTime: String, // ISO-8601 format (yyyy-MM-ddTHH:mm:ss)
    val endTime: String    // ISO-8601 format (yyyy-MM-ddTHH:mm:ss)
)

class AvailabilityService(
    private val httpClient: HttpClient,
    private val userService: UserService,
    private val apiBaseUrl: String = "http://10.0.2.2:8080/v1"
) {
    suspend fun createAvailability(date: LocalDate, startTimeString: String, endTimeString: String): AvailabilityDto {
        val currentUserId = userService.currentUser.value?.id 
            ?: throw IllegalStateException("User not logged in")
            
        // Parse the time strings (HH:mm format)
        val startComponents = startTimeString.split(":").map { it.toInt() }
        val endComponents = endTimeString.split(":").map { it.toInt() }
        
        if (startComponents.size != 2 || endComponents.size != 2) {
            throw IllegalArgumentException("Invalid time format. Use HH:mm format")
        }
        
        // Convert to ISO-8601 format (yyyy-MM-ddTHH:mm:ss)
        val startTimeIso = "${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}T${startComponents[0].toString().padStart(2, '0')}:${startComponents[1].toString().padStart(2, '0')}:00"
        val endTimeIso = "${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}T${endComponents[0].toString().padStart(2, '0')}:${endComponents[1].toString().padStart(2, '0')}:00"
        
        val request = AvailabilityRequest(
            employeeId = currentUserId,
            startTime = startTimeIso,
            endTime = endTimeIso
        )
        
        val response = httpClient.post("$apiBaseUrl/availabilities") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(AvailabilityRequest.serializer(), request))
            
            // Add authorization header if needed
            userService.authToken.value?.let { token ->
                header("Authorization", "Bearer $token")
            }
        }
        
        if (response.status.isSuccess()) {
            val responseBody = response.bodyAsText()
            return Json.decodeFromString(AvailabilityDto.serializer(), responseBody)
        } else {
            throw Exception("Failed to create availability: ${response.status}")
        }
    }
    
    suspend fun getUserAvailabilities(): List<AvailabilityDto> {
        // Get the current user ID
        val currentUserId = userService.currentUser.value?.id
        
        // Use the current user endpoint with userId parameter
        val response = httpClient.get("$apiBaseUrl/users/me/availabilities") {
            // Add the userId as a parameter if available
            currentUserId?.let {
                parameter("userId", it)
            }
            
            userService.authToken.value?.let { token ->
                header("Authorization", "Bearer $token")
            }
        }
        
        if (response.status.isSuccess()) {
            val responseBody = response.bodyAsText()
            return Json.decodeFromString<List<AvailabilityDto>>(responseBody)
        } else {
            throw Exception("Failed to fetch availabilities: ${response.status}")
        }
    }

    suspend fun updateAvailability(id: String, date: LocalDate, startTimeString: String, endTimeString: String): AvailabilityDto {
        val currentUserId = userService.currentUser.value?.id 
            ?: throw IllegalStateException("User not logged in")
            
        // Parse the time strings (HH:mm format)
        val startComponents = startTimeString.split(":").map { it.toInt() }
        val endComponents = endTimeString.split(":").map { it.toInt() }
        
        if (startComponents.size != 2 || endComponents.size != 2) {
            throw IllegalArgumentException("Invalid time format. Use HH:mm format")
        }
        
        // Convert to ISO-8601 format (yyyy-MM-ddTHH:mm:ss)
        val startTimeIso = "${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}T${startComponents[0].toString().padStart(2, '0')}:${startComponents[1].toString().padStart(2, '0')}:00"
        val endTimeIso = "${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}T${endComponents[0].toString().padStart(2, '0')}:${endComponents[1].toString().padStart(2, '0')}:00"
        
        val request = AvailabilityRequest(
            employeeId = currentUserId,
            startTime = startTimeIso,
            endTime = endTimeIso
        )
        
        val response = httpClient.put("$apiBaseUrl/availabilities/$id") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(AvailabilityRequest.serializer(), request))
            
            userService.authToken.value?.let { token ->
                header("Authorization", "Bearer $token")
            }
        }
        
        if (response.status.isSuccess()) {
            val responseBody = response.bodyAsText()
            return Json.decodeFromString(AvailabilityDto.serializer(), responseBody)
        } else {
            throw Exception("Failed to update availability: ${response.status}")
        }
    }

    suspend fun deleteAvailability(id: String): Boolean {
        val response = httpClient.delete("$apiBaseUrl/availabilities/$id") {
            userService.authToken.value?.let { token ->
                header("Authorization", "Bearer $token")
            }
        }
        
        return response.status.isSuccess()
    }
} 