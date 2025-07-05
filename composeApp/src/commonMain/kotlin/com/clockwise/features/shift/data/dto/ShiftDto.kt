package com.clockwise.features.shift.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShiftDto(
    val id: String,
    val scheduleId: String,
    val employeeId: String,
    val startTime: Double,
    val endTime: Double,
    val position: String? = null,
    val createdAt: Double,
    val updatedAt: Double,
    val workSession: WorkSessionDto? = null
)