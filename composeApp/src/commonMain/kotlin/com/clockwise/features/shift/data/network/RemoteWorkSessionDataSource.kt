package com.clockwise.features.shift.data.network

import com.clockwise.features.shift.data.dto.WorkSessionDto
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface RemoteWorkSessionDataSource {
    suspend fun clockIn(userId: String, shiftId: String): Result<WorkSessionDto, DataError.Remote>
    suspend fun clockOut(userId: String, shiftId: String): Result<WorkSessionDto, DataError.Remote>
    suspend fun saveSessionNote(workSessionId: String, note: String): Result<Unit, DataError.Remote>
} 