package com.clockwise.features.shift.core.data.network

import com.clockwise.features.shift.core.data.dto.ShiftDto
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface ShiftRepository {
    suspend fun getUpcomingShiftsForCurrentUser(): Flow<Result<List<ShiftDto>, DataError.Remote>>
    suspend fun getShiftsForWeek(weekStart: LocalDate): Flow<Result<List<ShiftDto>, DataError.Remote>>
} 