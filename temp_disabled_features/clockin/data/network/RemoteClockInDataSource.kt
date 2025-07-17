package com.clockwise.features.clockin.data.network

import com.clockwise.features.clockin.domain.model.ClockInRequest
import com.clockwise.features.clockin.domain.model.ClockInResponse

/**
 * Remote data source interface for clock-in operations.
 */
interface RemoteClockInDataSource {
    
    /**
     * Sends clock-in request to the server.
     */
    suspend fun clockIn(request: ClockInRequest): ClockInResponse
    
    /**
     * Checks if user is currently clocked in.
     */
    suspend fun isUserClockedIn(userId: String): Boolean
    
    /**
     * Clocks out the user.
     */
    suspend fun clockOut(userId: String): Boolean
}
