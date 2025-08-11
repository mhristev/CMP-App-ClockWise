package com.clockwise.features.shiftexchange.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.clockwise.features.shiftexchange.domain.model.ShiftRequest
import com.clockwise.features.shiftexchange.domain.model.RequestType
import com.clockwise.features.shiftexchange.domain.model.RequestStatus

@Serializable
data class ShiftRequestDto(
    val id: String,
    @SerialName("exchangeShiftId")
    val exchangeShiftId: String,
    @SerialName("requesterUserId")
    val requesterUserId: String,
    @SerialName("requestType")
    val requestType: RequestType,
    @SerialName("swapShiftId")
    val swapShiftId: String? = null,
    @SerialName("swapShiftPosition")
    val swapShiftPosition: String? = null,
    @SerialName("swapShiftStartTime")
    val swapShiftStartTime: String? = null, // ISO 8601 format from backend
    @SerialName("swapShiftEndTime")
    val swapShiftEndTime: String? = null, // ISO 8601 format from backend
    @SerialName("requesterUserFirstName")
    val requesterUserFirstName: String? = null,
    @SerialName("requesterUserLastName")
    val requesterUserLastName: String? = null,
    val status: RequestStatus,
    @SerialName("createdAt")
    val createdAt: String, // ISO 8601 format from backend
    @SerialName("updatedAt")
    val updatedAt: String  // ISO 8601 format from backend
)

@Serializable
data class CreateShiftRequestRequest(
    @SerialName("requestType")
    val requestType: RequestType,
    @SerialName("swapShiftId")
    val swapShiftId: String? = null,
    @SerialName("swapShiftPosition")
    val swapShiftPosition: String? = null,
    @SerialName("swapShiftStartTime")
    val swapShiftStartTime: String? = null, // ISO 8601 format
    @SerialName("swapShiftEndTime")
    val swapShiftEndTime: String? = null, // ISO 8601 format
    @SerialName("requesterUserFirstName")
    val requesterUserFirstName: String? = null,
    @SerialName("requesterUserLastName")
    val requesterUserLastName: String? = null
)

@Serializable
data class ShiftRequestListResponse(
    val shiftRequests: List<ShiftRequestDto>,
    val page: Int,
    val size: Int,
    val total: Long
)

// Extension functions to convert between DTO and domain models
fun ShiftRequestDto.toDomain(): ShiftRequest {
    return ShiftRequest(
        id = id,
        exchangeShiftId = exchangeShiftId,
        requesterUserId = requesterUserId,
        requestType = requestType,
        swapShiftId = swapShiftId,
        swapShiftPosition = swapShiftPosition,
        swapShiftStartTime = swapShiftStartTime?.let { Instant.parse(it).toLocalDateTime(TimeZone.currentSystemDefault()) },
        swapShiftEndTime = swapShiftEndTime?.let { Instant.parse(it).toLocalDateTime(TimeZone.currentSystemDefault()) },
        requesterFirstName = requesterUserFirstName,
        requesterLastName = requesterUserLastName,
        status = status,
        createdAt = Instant.parse(createdAt).toLocalDateTime(TimeZone.currentSystemDefault()),
        updatedAt = Instant.parse(updatedAt).toLocalDateTime(TimeZone.currentSystemDefault())
    )
}