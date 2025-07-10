package com.clockwise.features.shift.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleWithShiftsResponse(
    val id: String,
    val businessUnitId: String,
    val weekStart: Double, // Epoch seconds as Double from backend
    val status: String,
    val createdAt: Double, // Epoch seconds as Double from backend
    val updatedAt: Double, // Epoch seconds as Double from backend
    val shifts: List<ShiftDto>
) 