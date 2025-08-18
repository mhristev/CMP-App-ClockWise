package com.clockwise.features.managerapproval.domain.model

import kotlinx.datetime.Instant

data class PendingExchangeShift(
    val id: String,
    val requestId: String,
    val originalShiftId: String,
    val posterUserId: String,
    val posterUserFirstName: String?,
    val posterUserLastName: String?,
    val requesterUserId: String,
    val requesterUserFirstName: String?,
    val requesterUserLastName: String?,
    val shiftStartTime: Instant?,
    val shiftEndTime: Instant?,
    val shiftPosition: String?,
    val requestType: RequestType,
    val swapShiftId: String?,
    val swapShiftStartTime: Instant?,
    val swapShiftEndTime: Instant?,
    val swapShiftPosition: String?,
    val businessUnitId: String,
    val createdAt: Instant,
    val isExecutionPossible: Boolean?
)

enum class RequestType {
    TAKE_SHIFT,
    SWAP_SHIFT
}