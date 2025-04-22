package com.clockwise.features.welcome.domain.model

import kotlinx.datetime.LocalDateTime

data class Shift(
    val id: Int,
    val date: LocalDateTime,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val location: String = "",
    val clockInTime: LocalDateTime? = null,
    val clockOutTime: LocalDateTime? = null,
    val status: ShiftStatus = ShiftStatus.SCHEDULED
) 