package com.clockwise.features.shift.data.repository

import com.clockwise.features.shift.domain.model.WorkSession
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface WorkSessionRepository {
    suspend fun clockIn(userId: String, shiftId: String): Result<WorkSession, DataError.Remote>
    suspend fun clockOut(userId: String, shiftId: String): Result<WorkSession, DataError.Remote>
} 