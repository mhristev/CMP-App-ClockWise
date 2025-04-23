package com.clockwise.features.shift.domain.model

import kotlinx.datetime.LocalDateTime

data class Shift(
    val id: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val position: String = "",
    val clockInTime: LocalDateTime? = null,
    val clockOutTime: LocalDateTime? = null,
    val status: ShiftStatus = ShiftStatus.SCHEDULED,
    val employeeId: String

) 