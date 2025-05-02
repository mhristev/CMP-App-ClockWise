package com.clockwise.features.shift.domain.model

import kotlinx.datetime.LocalDateTime

data class WorkSession(
    val id: String,
    val userId: String,
    val shiftId: String,
    val clockInTime: LocalDateTime,
    val clockOutTime: LocalDateTime? = null,
    val totalMinutes: Int? = null,
    val status: WorkSessionStatus
)

enum class WorkSessionStatus {
    ACTIVE, COMPLETED, CANCELLED;
    
    companion object {
        fun fromString(status: String): WorkSessionStatus {
            return when (status.uppercase()) {
                "ACTIVE" -> ACTIVE
                "COMPLETED" -> COMPLETED
                "CANCELLED" -> CANCELLED
                else -> ACTIVE // Default
            }
        }
    }
} 