package com.clockwise.features.shift.data.repository

import com.clockwise.features.shift.data.dto.WorkSessionDto
import com.clockwise.features.shift.data.network.RemoteWorkSessionDataSource
import com.clockwise.features.shift.domain.model.WorkSession
import com.clockwise.features.shift.domain.model.WorkSessionStatus
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.datetime.*
import kotlinx.datetime.toLocalDateTime

class WorkSessionRepositoryImpl(
    private val remoteDataSource: RemoteWorkSessionDataSource
) : WorkSessionRepository {

    override suspend fun clockIn(userId: String, shiftId: String): Result<WorkSession, DataError.Remote> {
        return when (val result = remoteDataSource.clockIn(userId, shiftId)) {
            is Result.Success -> {
                try {
                    println("ClockIn SUCCESS response: ${result.data}")
                    Result.Success(mapToWorkSession(result.data))
                } catch (e: Exception) {
                    println("Error mapping WorkSessionDto to WorkSession: ${e.message}")
                    Result.Error(DataError.Remote.SERIALIZATION)
                }
            }
            is Result.Error -> {
                println("ClockIn ERROR: ${result.error}")
                Result.Error(result.error)
            }
        }
    }

    override suspend fun clockOut(userId: String, shiftId: String): Result<WorkSession, DataError.Remote> {
        return when (val result = remoteDataSource.clockOut(userId, shiftId)) {
            is Result.Success -> {
                try {
                    println("ClockOut SUCCESS response: ${result.data}")
                    Result.Success(mapToWorkSession(result.data))
                } catch (e: Exception) {
                    println("Error mapping WorkSessionDto to WorkSession: ${e.message}")
                    Result.Error(DataError.Remote.SERIALIZATION)
                }
            }
            is Result.Error -> {
                println("ClockOut ERROR: ${result.error}")
                Result.Error(result.error)
            }
        }
    }
    
    private fun mapToWorkSession(dto: WorkSessionDto): WorkSession {
        println("Mapping DTO: id=${dto.id}, userId=${dto.userId}, shiftId=${dto.shiftId}, " +
                "clockInTime=${dto.clockInTime}, clockOutTime=${dto.clockOutTime}, " +
                "totalMinutes=${dto.totalMinutes}, status=${dto.status}")
        
        return WorkSession(
            id = dto.id,
            userId = dto.userId,
            shiftId = dto.shiftId,
            clockInTime = dto.clockInTime?.let { timestampToLocalDateTime(it) },
            clockOutTime = dto.clockOutTime?.let { timestampToLocalDateTime(it) },
            totalMinutes = dto.totalMinutes,
            status = WorkSessionStatus.fromString(dto.status)
        )
    }
    
    /**
     * Converts a timestamp (seconds since epoch) to LocalDateTime
     */
    private fun timestampToLocalDateTime(timestamp: Double): LocalDateTime {
        try {
            println("Converting timestamp: $timestamp")
            // Convert seconds to milliseconds and then to Instant
            val seconds = timestamp.toLong()
            val nanos = ((timestamp - seconds) * 1_000_000_000).toInt()
            val instant = Instant.fromEpochSeconds(seconds, nanos)
            
            // Convert to LocalDateTime in system timezone
            return instant.toLocalDateTime(TimeZone.currentSystemDefault())
        } catch (e: Exception) {
            println("Error converting timestamp: ${e.message}")
            // Fallback to current time if conversion fails
            return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }
} 