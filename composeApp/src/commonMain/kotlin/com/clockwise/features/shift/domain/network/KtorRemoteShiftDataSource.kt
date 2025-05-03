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
        
        return safeCall {
            httpClient.get("${apiConfig.baseShiftUrl}/users/$userId/shifts/upcoming") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
            }
        }
    }

    override suspend fun getShiftsForWeek(weekStart: LocalDate): Result<List<ShiftDto>, DataError.Remote> {
        val businessUnitId = userService.getCurrentUserBusinessUnitId()
            ?: return Result.Error(DataError.Remote.UNKNOWN)
        
        val token = userService.authToken.value
            ?: return Result.Error(DataError.Remote.UNKNOWN)
        
        // Convert LocalDate to ISO string format with timezone offset
        // Format: 2023-01-01T00:00:00+03:00
        val timezone = TimeProvider.getLocalTimezoneOffset()
        val dateTime = "$weekStart" + "T00:00:00" + timezone
        
        println("Requesting shifts for week starting at: $dateTime")
        
        try {
            return safeCall {
                httpClient.get("${apiConfig.baseShiftUrl}/business-units/$businessUnitId/shifts/week") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $token")
                    parameter("weekStart", dateTime) // Ktor will handle URL encoding for us
                }
            }
        } catch (e: Exception) {
            println("Error fetching weekly shifts: ${e.message}")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
    }
} 