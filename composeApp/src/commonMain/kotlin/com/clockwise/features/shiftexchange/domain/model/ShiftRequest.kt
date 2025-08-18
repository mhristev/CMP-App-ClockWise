package com.clockwise.features.shiftexchange.domain.model

import kotlinx.datetime.LocalDateTime

data class ShiftRequest(
    val id: String,
    val exchangeShiftId: String,
    val requesterUserId: String,
    val requestType: RequestType,
    val swapShiftId: String? = null,
    val status: RequestStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    // Additional requester details for display
    val requesterFirstName: String? = null,
    val requesterLastName: String? = null,
    // Additional swap shift details if applicable
    val swapShiftStartTime: LocalDateTime? = null,
    val swapShiftEndTime: LocalDateTime? = null,
    val swapShiftPosition: String? = null,
    // Conflict detection result
    val isExecutionPossible: Boolean? = null
) {
    val requesterName: String
        get() = if (requesterFirstName != null && requesterLastName != null) {
            "$requesterFirstName $requesterLastName"
        } else {
            "Unknown User"
        }
    
    fun isSwapRequest(): Boolean {
        return requestType == RequestType.SWAP_SHIFT
    }
    
    fun isTakeRequest(): Boolean {
        return requestType == RequestType.TAKE_SHIFT
    }
    
    fun isPending(): Boolean {
        return status == RequestStatus.PENDING
    }
    
    fun isAcceptedByPoster(): Boolean {
        return status == RequestStatus.ACCEPTED_BY_POSTER
    }
    
    fun isAwaitingManagerApproval(): Boolean {
        return status == RequestStatus.ACCEPTED_BY_POSTER
    }
    
    fun isCompleted(): Boolean {
        return status in listOf(
            RequestStatus.APPROVED_BY_MANAGER,
            RequestStatus.REJECTED_BY_MANAGER,
            RequestStatus.DECLINED_BY_POSTER
        )
    }
}

enum class RequestType {
    TAKE_SHIFT,
    SWAP_SHIFT
}

enum class RequestStatus {
    PENDING,
    ACCEPTED_BY_POSTER,
    DECLINED_BY_POSTER,
    APPROVED_BY_MANAGER,
    REJECTED_BY_MANAGER
}