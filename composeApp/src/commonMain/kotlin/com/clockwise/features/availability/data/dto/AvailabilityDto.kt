package com.clockwise.features.availability.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AvailabilityDto(
    val id: String? = null,
    val employeeId: String,
    val startTime: List<Int>, // [year, month, day, hour, minute]
    val endTime: List<Int>,   // [year, month, day, hour, minute]
    val businessUnitId: String? = null,
    val createdAt: List<Int>? = null, // [year, month, day, hour, minute, second, nano]
    val updatedAt: List<Int>? = null  // [year, month, day, hour, minute, second, nano]
)

@Serializable
data class AvailabilityRequest(
    val employeeId: String,
    val startTime: String, // ISO-8601 format (yyyy-MM-ddTHH:mm:ss)
    val endTime: String,   // ISO-8601 format (yyyy-MM-ddTHH:mm:ss)
    val businessUnitId: String? = null
) 