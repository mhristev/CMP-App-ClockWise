package com.clockwise.features.shift.domain.model

import kotlinx.datetime.LocalDateTime

data class WorkSession(
    val id: String,
    val userId: String,
    val shiftId: String,
    val clockInTime: LocalDateTime?,
    val clockOutTime: LocalDateTime? = null,
    val totalMinutes: Int? = null,
    val status: WorkSessionStatus,
    val sessionNote: SessionNote? = null
)

data class SessionNote(
    val id: String?,
    val workSessionId: String,
    val content: String,
    val createdAt: LocalDateTime
)

enum class WorkSessionStatus {
    CREATED, ACTIVE, COMPLETED, CANCELLED;
    
    companion object {
        fun fromString(status: String): WorkSessionStatus {
            return when (status.uppercase()) {
                "CREATED" -> CREATED
                "ACTIVE" -> ACTIVE
                "COMPLETED" -> COMPLETED
                "CANCELLED" -> CANCELLED
                else -> CREATED // Default to CREATED instead of ACTIVE
            }
        }
    }
} 