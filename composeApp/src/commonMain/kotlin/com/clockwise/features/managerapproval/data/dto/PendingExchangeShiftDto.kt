package com.clockwise.features.managerapproval.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PendingExchangeShiftDto(
    val exchangeShift: ExchangeShiftDto,
    val acceptedRequest: ShiftRequestDto
)

@Serializable
data class ExchangeShiftDto(
    val id: String,
    val planningServiceShiftId: String,
    val posterUserId: String,
    val businessUnitId: String,
    val status: String,
    val acceptedRequestId: String?,
    val shiftPosition: String?,
    val shiftStartTime: String?,
    val shiftEndTime: String?,
    val userFirstName: String?,
    val userLastName: String?,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class ShiftRequestDto(
    val id: String,
    val exchangeShiftId: String,
    val requesterUserId: String,
    val requestType: String,
    val swapShiftId: String?,
    val swapShiftPosition: String?,
    val swapShiftStartTime: String?,
    val swapShiftEndTime: String?,
    val requesterUserFirstName: String?,
    val requesterUserLastName: String?,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)