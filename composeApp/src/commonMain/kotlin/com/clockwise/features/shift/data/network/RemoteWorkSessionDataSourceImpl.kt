package com.clockwise.features.shift.data.network

import com.clockwise.core.di.ApiConfig
import com.clockwise.features.shift.data.dto.WorkSessionDto
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
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
    private val apiConfig: ApiConfig
) : RemoteWorkSessionDataSource {

    override suspend fun clockIn(userId: String, shiftId: String): Result<WorkSessionDto, DataError.Remote> {
        return safeCall {
            client.post("${apiConfig.baseWorkSessionUrl}/clock-in") {
                contentType(ContentType.Application.Json)
                setBody(ClockInOutRequest(userId, shiftId))
            }
        }
    }

    override suspend fun clockOut(userId: String, shiftId: String): Result<WorkSessionDto, DataError.Remote> {
        return safeCall {
            client.post("${apiConfig.baseWorkSessionUrl}/clock-out") {
                contentType(ContentType.Application.Json)
                setBody(ClockInOutRequest(userId, shiftId))
            }
        }
    }

    override suspend fun saveSessionNote(workSessionId: String, note: String): Result<Unit, DataError.Remote> {
        return safeCall {
            client.put("${apiConfig.baseWorkSessionUrl}/management/session-note") {
                contentType(ContentType.Application.Json)
                setBody(SessionNoteRequest(workSessionId, note))
            }
        }
    }
} 