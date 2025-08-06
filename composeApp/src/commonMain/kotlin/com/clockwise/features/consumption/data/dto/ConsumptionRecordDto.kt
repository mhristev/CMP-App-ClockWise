package com.clockwise.features.consumption.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConsumptionRecordDto(
    val id: String?,
    val consumptionItemId: String,
    val consumptionItemName: String?,
    val userId: String,
    val workSessionId: String,
    val quantity: Double,
    val consumedAt: String // ISO format string for LocalDateTime
)