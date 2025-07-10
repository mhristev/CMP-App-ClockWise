package com.clockwise.features.shift.domain.repositories

import com.clockwise.features.shift.data.dto.ShiftDto
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface ShiftRepository {
    suspend fun getUpcomingShifts(): Flow<Result<List<ShiftDto>, DataError.Remote>>
    suspend fun clockIn(shiftId: String): Flow<Result<Unit, DataError.Remote>>
    suspend fun clockOut(shiftId: String): Flow<Result<Unit, DataError.Remote>>
    suspend fun getShiftsForWeek(weekStart: LocalDate): Flow<Result<List<ShiftDto>, DataError.Remote>>

    suspend fun saveSessionNote(workSessionId: String, note: String): Flow<Result<Unit, DataError.Remote>>
} 