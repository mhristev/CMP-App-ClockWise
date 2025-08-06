package com.clockwise.features.consumption.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConsumptionItemUsageDto(
    val consumptionItemId: String,
    val quantity: Double
)