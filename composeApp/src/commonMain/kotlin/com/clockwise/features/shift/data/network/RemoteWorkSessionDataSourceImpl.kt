package com.clockwise.features.shift.data.network

import com.clockwise.core.di.ApiConfig
import com.clockwise.features.auth.UserService
import com.clockwise.features.shift.data.dto.WorkSessionDto
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class ClockInOutRequest(
    val userId: String,
    val shiftId: String
)

@Serializable
data class SessionNoteRequest(
    val workSessionId: String,
    val content: String
)

class RemoteWorkSessionDataSourceImpl(
    private val client: HttpClient,
    private val apiConfig: ApiConfig,
    private val userService: UserService
) : RemoteWorkSessionDataSource {

    override suspend fun clockIn(userId: String, shiftId: String): Result<WorkSessionDto, DataError.Remote> {
        val token = userService.authToken.value
            ?: return Result.Error(DataError.Remote.UNKNOWN)
            
        return safeCall {
            client.post("${apiConfig.baseWorkSessionUrl}/clock-in") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(ClockInOutRequest(userId, shiftId))
            }
        }
    }

    override suspend fun clockOut(userId: String, shiftId: String): Result<WorkSessionDto, DataError.Remote> {
        val token = userService.authToken.value
            ?: return Result.Error(DataError.Remote.UNKNOWN)
            
        return safeCall {
            client.post("${apiConfig.baseWorkSessionUrl}/clock-out") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(ClockInOutRequest(userId, shiftId))
            }
        }
    }

    override suspend fun saveSessionNote(workSessionId: String, note: String): Result<Unit, DataError.Remote> {
        val token = userService.authToken.value
            ?: return Result.Error(DataError.Remote.UNKNOWN)
            
        return safeCall {
            client.put("${apiConfig.baseShiftUrl}/session-notes/upsert") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(SessionNoteRequest(workSessionId, note))
            }
        }
    }
} 