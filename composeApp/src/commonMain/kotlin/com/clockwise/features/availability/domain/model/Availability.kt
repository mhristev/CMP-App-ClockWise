package com.clockwise.features.availability.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Domain model representing user availability
 */
data class Availability(
    val id: String,
    val employeeId: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val businessUnitId: String? = null
) 