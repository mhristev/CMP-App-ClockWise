package com.clockwise.features.shift.schedule.domain

import com.clockwise.core.UserService
import com.clockwise.core.di.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class ShiftDto(
    val id: String,
    val scheduleId: String,
    val employeeId: String,
    val startTime: List<Int>,  // [year, month, day, hour, minute]
    val endTime: List<Int>,    // [year, month, day, hour, minute]
    val position: String? = null,
    val createdAt: List<Int>,  // [year, month, day, hour, minute, second, nanos]
    val updatedAt: List<Int>   // [year, month, day, hour, minute, second, nanos]
)

class ShiftRepository(
    private val httpClient: HttpClient,
    private val userService: UserService,
    private val apiConfig: ApiConfig
) {
    suspend fun getUpcomingShiftsForCurrentUser(): List<ShiftDto> {
        val userId = userService.currentUser.value?.id ?: return emptyList()
        
        return try {
            val token = userService.authToken.value ?: return emptyList()
            
            val result = httpClient.get("${apiConfig.baseShiftUrl}/users/$userId/shifts/upcoming") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
            }.body<List<ShiftDto>>()
            
            println("Retrieved ${result.size} upcoming shifts for current user")
            result
        } catch (e: Exception) {
            println("Error fetching upcoming shifts: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun getShiftsForDay(date: LocalDate): List<ShiftDto> {
        val businessUnitId = userService.getCurrentUserBusinessUnitId() ?: return emptyList()
        
        // Convert LocalDate to ISO string format
        val dateTime = "$date" + "T00:00:00"
        
        return try {
            val token = userService.authToken.value ?: return emptyList()
            
            val result = httpClient.get("${apiConfig.baseShiftUrl}/business-units/$businessUnitId/shifts/day?date=$dateTime") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
            }.body<List<ShiftDto>>()
            
            println("Retrieved ${result.size} shifts for day")
            result
        } catch (e: Exception) {
            // Handle errors
            println("Error fetching shifts for day: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun getShiftsForWeek(weekStart: LocalDate): List<ShiftDto> {
        val businessUnitId = userService.getCurrentUserBusinessUnitId() ?: return emptyList()
        
        // Convert LocalDate to ISO string format
        val dateTime = "$weekStart" + "T00:00:00"
        
        return try {
            val token = userService.authToken.value ?: return emptyList()
            
            val result = httpClient.get("${apiConfig.baseShiftUrl}/business-units/$businessUnitId/shifts/week?weekStart=$dateTime") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
            }.body<List<ShiftDto>>()
            
            println("Retrieved ${result.size} shifts for week")
            result
        } catch (e: Exception) {
            // Handle errors
            println("Error fetching shifts for week: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
} 