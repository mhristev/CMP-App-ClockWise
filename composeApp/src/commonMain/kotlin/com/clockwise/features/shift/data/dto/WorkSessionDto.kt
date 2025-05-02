package com.clockwise.features.shift.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorkSessionDto(
    val id: String,
    val userId: String,
    val shiftId: String,
    val clockInTime: Double,
    val clockOutTime: Double? = null,
    val totalMinutes: Int? = null,
    val status: String
) 