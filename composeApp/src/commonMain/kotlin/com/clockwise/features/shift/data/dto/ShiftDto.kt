package com.clockwise.features.shift.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShiftDto(
    val id: String,
    val scheduleId: String,
    val employeeId: String,
    val startTime: Double,  // Epoch seconds timestamp with decimal precision
    val endTime: Double,    // Epoch seconds timestamp with decimal precision
    val position: String? = null,
    val createdAt: Double? = null,  // Epoch seconds timestamp (optional)
    val updatedAt: Double? = null   // Epoch seconds timestamp (optional)
) 