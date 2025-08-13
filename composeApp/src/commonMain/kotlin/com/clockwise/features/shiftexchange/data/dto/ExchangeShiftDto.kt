package com.clockwise.features.shiftexchange.data.dto

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.clockwise.features.shiftexchange.domain.model.ExchangeShift
import com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus
import kotlinx.serialization.SerialName

@Serializable
data class ExchangeShiftDto(
    val id: String,
    @SerialName("planningServiceShiftId")
    val planningServiceShiftId: String,
    @SerialName("posterUserId")
    val posterUserId: String,
    @SerialName("businessUnitId")
    val businessUnitId: String,
    val status: ExchangeShiftStatus,
    @SerialName("acceptedRequestId")
    val acceptedRequestId: String? = null,
    @SerialName("shiftPosition")
    val shiftPosition: String?,
    @SerialName("shiftStartTime")
    val shiftStartTime: String?, // ISO 8601 format from backend
    @SerialName("shiftEndTime")
    val shiftEndTime: String?, // ISO 8601 format from backend
    @SerialName("userFirstName")
    val userFirstName: String?,
    @SerialName("userLastName")
    val userLastName: String?,
    @SerialName("createdAt")
    val createdAt: String, // ISO 8601 format from backend
    @SerialName("updatedAt")
    val updatedAt: String  // ISO 8601 format from backend
)

@Serializable
data class CreateExchangeShiftRequest(
    @SerialName("planningServiceShiftId")
    val planningServiceShiftId: String,
    @SerialName("businessUnitId")
    val businessUnitId: String,
    @SerialName("shiftPosition")
    val shiftPosition: String,
    @SerialName("shiftStartTime")
    val shiftStartTime: String, // ISO 8601 format
    @SerialName("shiftEndTime")
    val shiftEndTime: String, // ISO 8601 format
    @SerialName("userId")
    val userId: String, // Application user ID (not Keycloak ID)
    @SerialName("userFirstName")
    val userFirstName: String,
    @SerialName("userLastName")
    val userLastName: String
)

@Serializable
data class ExchangeShiftListResponse(
    val exchangeShifts: List<ExchangeShiftDto>,
    val page: Int,
    val size: Int,
    val total: Long
)

// Extension functions to convert between DTO and domain models
fun ExchangeShiftDto.toDomain(): ExchangeShift {
    return ExchangeShift(
        id = id,
        planningServiceShiftId = planningServiceShiftId,
        posterUserId = posterUserId,
        businessUnitId = businessUnitId,
        status = status,
        acceptedRequestId = acceptedRequestId,
        shiftStartTime = shiftStartTime?.let { Instant.parse(it).toLocalDateTime(TimeZone.currentSystemDefault()) },
        shiftEndTime = shiftEndTime?.let { Instant.parse(it).toLocalDateTime(TimeZone.currentSystemDefault()) },
        position = shiftPosition,
        posterFirstName = userFirstName,
        posterLastName = userLastName,
        createdAt = Instant.parse(createdAt).toLocalDateTime(TimeZone.currentSystemDefault()),
        updatedAt = Instant.parse(updatedAt).toLocalDateTime(TimeZone.currentSystemDefault())
    )
}