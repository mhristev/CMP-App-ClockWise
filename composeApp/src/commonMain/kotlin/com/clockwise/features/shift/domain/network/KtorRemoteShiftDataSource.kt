package com.clockwise.features.shift.domain.network

import com.clockwise.features.auth.UserService
import com.clockwise.core.di.ApiConfig
import com.clockwise.core.TimeProvider
import com.clockwise.features.shift.data.dto.ShiftDto
import com.clockwise.features.shift.data.dto.ScheduleWithShiftsResponse
import com.clockwise.features.shift.data.dto.ErrorResponse
import com.clockwise.features.shift.data.network.RemoteShiftDataSource
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json

class KtorRemoteShiftDataSource(
    private val httpClient: HttpClient,
    private val userService: UserService,
    private val apiConfig: ApiConfig
) : RemoteShiftDataSource {

    override suspend fun getUpcomingShiftsForCurrentUser(): Result<List<ShiftDto>, DataError.Remote> {
        val userId = userService.currentUser.value?.id
            ?: return Result.Error(DataError.Remote.UNKNOWN)
        
        val token = userService.getValidAuthToken()
            ?: return Result.Error(DataError.Remote.UNKNOWN)
        
        println("DEBUG: getUpcomingShiftsForCurrentUser - Requesting shifts for user: $userId")
        val url = "${apiConfig.baseShiftUrl}/users/$userId/shifts/upcoming"
        println("DEBUG: getUpcomingShiftsForCurrentUser - URL: $url")
        println("DEBUG: getUpcomingShiftsForCurrentUser - Authorization: Bearer $token")

        return safeCall {
            val response = httpClient.get(url) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
            }
            val responseBody = response.bodyAsText()
            println("DEBUG: getUpcomingShiftsForCurrentUser - Raw Response: $responseBody")
            response
        }
    }

    override suspend fun getShiftsForWeek(weekStart: LocalDate): Result<List<ShiftDto>, DataError.Remote> {
        val businessUnitId = userService.currentUser.value?.businessUnitId
            ?: return Result.Error(DataError.Remote.UNKNOWN)
        
        val token = userService.getValidAuthToken()
            ?: return Result.Error(DataError.Remote.UNKNOWN)
        
        // Format as LocalDate for the published endpoint (no timezone needed)
        val weekStartString = weekStart.toString()
        
        println("DEBUG: getShiftsForWeek - Requesting PUBLISHED shifts for week starting at: $weekStartString")
        val url = "${apiConfig.baseShiftUrl}/business-units/$businessUnitId/schedules/week/published"
        println("DEBUG: getShiftsForWeek - URL: $url")
        println("DEBUG: getShiftsForWeek - Authorization: Bearer $token")
        println("DEBUG: getShiftsForWeek - Parameter weekStart: $weekStartString")

        try {
            val response = httpClient.get(url) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                parameter("weekStart", weekStartString)
            }
            
            val responseBody = response.bodyAsText()
            println("DEBUG: getShiftsForWeek - Raw Response: $responseBody")
            println("DEBUG: getShiftsForWeek - Response Status: ${response.status.value}")
            
            // Check if response is successful
            return if (response.status.value in 200..299) {
                // Parse successful response
                try {
                    val json = Json { ignoreUnknownKeys = true }
                    val scheduleResponse = json.decodeFromString<ScheduleWithShiftsResponse>(responseBody)
                    
                    println("DEBUG: getShiftsForWeek - Successfully parsed ScheduleWithShiftsResponse")
                    println("DEBUG: getShiftsForWeek - Schedule ID: ${scheduleResponse.id}")
                    println("DEBUG: getShiftsForWeek - Schedule Status: ${scheduleResponse.status}")
                    println("DEBUG: getShiftsForWeek - Number of shifts: ${scheduleResponse.shifts.size}")
                    
                    Result.Success(scheduleResponse.shifts)
                } catch (e: Exception) {
                    println("DEBUG: getShiftsForWeek - Error parsing successful response: ${e.message}")
                    Result.Error(DataError.Remote.SERIALIZATION)
                }
            } else {
                // Parse error response
                try {
                    val json = Json { ignoreUnknownKeys = true }
                    val errorResponse = json.decodeFromString<ErrorResponse>(responseBody)
                    
                    println("DEBUG: getShiftsForWeek - Parsed error response: ${errorResponse.message}")
                    
                    // Check if this is a "schedule not published" error
                    if (response.status.value == 400 && 
                        errorResponse.message.contains("is not published", ignoreCase = true)) {
                        println("DEBUG: getShiftsForWeek - Detected 'schedule not published' error")
                        Result.Error(DataError.Remote.SCHEDULE_NOT_PUBLISHED)
                    } else {
                        println("DEBUG: getShiftsForWeek - Other error: ${errorResponse.error}")
                        when (response.status.value) {
                            408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
                            429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
                            in 500..599 -> Result.Error(DataError.Remote.SERVER)
                            else -> Result.Error(DataError.Remote.UNKNOWN)
                        }
                    }
                } catch (e: Exception) {
                    println("DEBUG: getShiftsForWeek - Error parsing error response: ${e.message}")
                    // Fallback - check if response contains "not published" in raw text
                    if (responseBody.contains("is not published", ignoreCase = true)) {
                        println("DEBUG: getShiftsForWeek - Detected 'schedule not published' from raw response")
                        Result.Error(DataError.Remote.SCHEDULE_NOT_PUBLISHED)
                    } else {
                        Result.Error(DataError.Remote.UNKNOWN)
                    }
                }
            }
        } catch (e: Exception) {
            println("DEBUG: getShiftsForWeek - Error fetching weekly shifts: ${e.message}")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
    }
}