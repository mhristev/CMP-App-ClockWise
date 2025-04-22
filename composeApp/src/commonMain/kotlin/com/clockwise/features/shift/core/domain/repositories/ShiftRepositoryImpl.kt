package com.clockwise.features.shift.core.domain.repositories

import com.clockwise.features.shift.core.data.dto.ShiftDto
import com.clockwise.features.shift.core.data.network.RemoteShiftDataSource
import com.clockwise.features.shift.core.data.network.ShiftRepository
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
            val result = remoteDataSource.getUpcomingShiftsForCurrentUser()
            emit(result)
        }
    }
    
    override suspend fun getShiftsForWeek(weekStart: LocalDate): Flow<Result<List<ShiftDto>, DataError.Remote>> {
        return flow {
            val result = remoteDataSource.getShiftsForWeek(weekStart)
            emit(result)
        }
    }
} 