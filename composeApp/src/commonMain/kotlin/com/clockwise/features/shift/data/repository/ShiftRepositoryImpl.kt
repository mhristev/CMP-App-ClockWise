package com.clockwise.features.shift.data.repository

import com.clockwise.features.shift.data.dto.ShiftDto
import com.clockwise.features.shift.data.network.RemoteShiftDataSource
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate

class ShiftRepositoryImpl(
    private val remoteDataSource: RemoteShiftDataSource
) : ShiftRepository {

    override suspend fun getUpcomingShiftsForCurrentUser(): Flow<Result<List<ShiftDto>, DataError.Remote>> {
        return flow {
            emit(remoteDataSource.getUpcomingShiftsForCurrentUser())
        }
    }

    override suspend fun getShiftsForWeek(weekStart: LocalDate): Flow<Result<List<ShiftDto>, DataError.Remote>> {
        return flow {
            emit(remoteDataSource.getShiftsForWeek(weekStart))
        }
    }

    override suspend fun saveSessionNote(workSessionId: String, note: String): Flow<Result<Unit, DataError.Remote>> {
        return flow {
            // This method should be implemented in RemoteWorkSessionDataSource
            emit(Result.Success(Unit))
        }
    }
} 