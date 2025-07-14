package com.clockwise.features.clockin.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ClockInRequest(
    val userId: String,
    val businessUnitId: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float?,
    val timestamp: Long
)

@Serializable
data class ClockInResponse(
    val id: String,
    val userId: String,
    val businessUnitId: String,
    val clockInTime: Long,
    val location: ClockInLocation,
    val status: ClockInStatus
)

@Serializable
data class ClockInLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float?
)

enum class ClockInStatus {
    SUCCESS,
    FAILED_TOO_FAR,
    FAILED_INVALID_LOCATION,
    FAILED_PERMISSION_DENIED
}
