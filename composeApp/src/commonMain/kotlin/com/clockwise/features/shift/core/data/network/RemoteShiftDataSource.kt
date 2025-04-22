package com.clockwise.features.shift.core.data.network

import com.clockwise.features.shift.core.data.dto.ShiftDto
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.datetime.LocalDate

interface RemoteShiftDataSource {
    suspend fun getUpcomingShiftsForCurrentUser(): Result<List<ShiftDto>, DataError.Remote>
    suspend fun getShiftsForWeek(weekStart: LocalDate): Result<List<ShiftDto>, DataError.Remote>
} 