package com.clockwise.features.shiftexchange.domain.model

import kotlinx.datetime.LocalDateTime

data class ExchangeShift(
    val id: String,
    val planningServiceShiftId: String,
    val posterUserId: String,
    val businessUnitId: String,
    val status: ExchangeShiftStatus,
    val acceptedRequestId: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    // Additional shift details for display
    val shiftStartTime: LocalDateTime? = null,
    val shiftEndTime: LocalDateTime? = null,
    val position: String? = null,
    val posterFirstName: String? = null,
    val posterLastName: String? = null
) {
    val posterName: String
        get() = if (posterFirstName != null && posterLastName != null) {
            "$posterFirstName $posterLastName"
        } else {
            "Unknown User"
        }
    
    fun canAcceptRequests(): Boolean {
        return status == ExchangeShiftStatus.OPEN
    }
    
    fun canBeModifiedByPoster(): Boolean {
        return status in listOf(ExchangeShiftStatus.OPEN, ExchangeShiftStatus.PENDING_SELECTION)
    }
    
    fun isPendingManagerApproval(): Boolean {
        return status == ExchangeShiftStatus.AWAITING_MANAGER_APPROVAL
    }
    
    fun isCompleted(): Boolean {
        return status in listOf(
            ExchangeShiftStatus.APPROVED, 
            ExchangeShiftStatus.REJECTED, 
            ExchangeShiftStatus.CANCELLED
        )
    }
}

enum class ExchangeShiftStatus {
    OPEN,
    PENDING_SELECTION,
    AWAITING_MANAGER_APPROVAL,
    APPROVED,
    REJECTED,
    CANCELLED
}