package com.clockwise.features.availability.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
data class AvailabilityDto(
    val id: String? = null,
    val employeeId: String,
    // Use Double for timestamp values instead of String
    val startTime: Double,
    val endTime: Double,
    val businessUnitId: String? = null,
    val createdAt: Double? = null,
    val updatedAt: Double? = null
)

@Serializable
data class AvailabilityRequest(
    val employeeId: String,
    val startTime: String, // Keep as ISO-8601 format for requests
    val endTime: String,   // Keep as ISO-8601 format for requests
    val businessUnitId: String? = null
) 