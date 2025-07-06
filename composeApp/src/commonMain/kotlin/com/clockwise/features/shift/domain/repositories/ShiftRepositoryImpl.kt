package com.clockwise.features.shift.domain.repositories

import com.clockwise.features.auth.UserService
import com.clockwise.features.shift.data.dto.ShiftDto
import com.clockwise.features.shift.data.network.RemoteShiftDataSource
import com.clockwise.features.shift.data.network.RemoteWorkSessionDataSource
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate

class ShiftRepositoryImpl(
    private val remoteDataSource: RemoteShiftDataSource,
    private val remoteWorkSessionDataSource: RemoteWorkSessionDataSource,
    private val userService: UserService
) : ShiftRepository {

    override suspend fun getUpcomingShifts(): Flow<Result<List<ShiftDto>, DataError.Remote>> {
        return flow {
            emit(remoteDataSource.getUpcomingShiftsForCurrentUser())
        }
    }

    override suspend fun getShiftsForWeek(weekStart: String): Flow<Result<List<ShiftDto>, DataError.Remote>> {
        return flow {
            val date = LocalDate.parse(weekStart)
            emit(remoteDataSource.getShiftsForWeek(date))
        }
    }

    override suspend fun clockIn(shiftId: String): Flow<Result<Unit, DataError.Remote>> {
        return flow {
            val userId = userService.currentUser.value?.id
            if (userId != null) {
                emit(remoteWorkSessionDataSource.clockIn(userId, shiftId).map { })
            }
        }
    }

    override suspend fun clockOut(shiftId: String): Flow<Result<Unit, DataError.Remote>> {
        return flow {
            val userId = userService.currentUser.value?.id
            if (userId != null) {
                emit(remoteWorkSessionDataSource.clockOut(userId, shiftId).map { })
            }
        }
    }

    override suspend fun saveSessionNote(workSessionId: String, note: String): Flow<Result<Unit, DataError.Remote>> {
        return flow {
            emit(remoteWorkSessionDataSource.saveSessionNote(workSessionId, note))
        }
    }
} 