package com.clockwise.features.shift.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShiftDto(
    val id: String,
    val scheduleId: String,
    val employeeId: String,
    val startTime: List<Int>,  // [year, month, day, hour, minute]
    val endTime: List<Int>,    // [year, month, day, hour, minute]
    val position: String? = null,
    val createdAt: List<Int>,  // [year, month, day, hour, minute, second, nanos]
    val updatedAt: List<Int>   // [year, month, day, hour, minute, second, nanos]
) 