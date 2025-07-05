package com.clockwise.features.shift.domain.network

import com.clockwise.features.auth.UserService
import com.clockwise.core.di.ApiConfig
import com.clockwise.core.TimeProvider
import com.clockwise.features.shift.data.dto.ShiftDto
import com.clockwise.features.shift.data.network.RemoteShiftDataSource
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.datetime.LocalDate
import io.ktor.client.statement.bodyAsText

class KtorRemoteShiftDataSource(
    private val httpClient: HttpClient,
    private val userService: UserService,
    private val apiConfig: ApiConfig
) : RemoteShiftDataSource {

    override suspend fun getUpcomingShiftsForCurrentUser(): Result<List<ShiftDto>, DataError.Remote> {
        val userId = userService.currentUser.value?.id
            ?: return Result.Error(DataError.Remote.UNKNOWN)
        
        val token = userService.authToken.value
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
        
        val token = userService.authToken.value
            ?: return Result.Error(DataError.Remote.UNKNOWN)
        
        // Convert LocalDate to ISO string format with timezone offset
        // Format: 2023-01-01T00:00:00+03:00
        val timezone = TimeProvider.getLocalTimezoneOffset()
        val dateTime = "$weekStart" + "T00:00:00" + timezone
        
        println("DEBUG: getShiftsForWeek - Requesting shifts for week starting at: $dateTime")
        val url = "${apiConfig.baseShiftUrl}/business-units/$businessUnitId/shifts/week"
        println("DEBUG: getShiftsForWeek - URL: $url")
        println("DEBUG: getShiftsForWeek - Authorization: Bearer $token")
        println("DEBUG: getShiftsForWeek - Parameter weekStart: $dateTime")

        try {
            return safeCall {
                val response = httpClient.get(url) {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $token")
                    parameter("weekStart", dateTime)
                }
                val responseBody = response.bodyAsText()
                println("DEBUG: getShiftsForWeek - Raw Response: $responseBody")
                response
            }
        } catch (e: Exception) {
            println("DEBUG: getShiftsForWeek - Error fetching weekly shifts: ${e.message}")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
    }
}