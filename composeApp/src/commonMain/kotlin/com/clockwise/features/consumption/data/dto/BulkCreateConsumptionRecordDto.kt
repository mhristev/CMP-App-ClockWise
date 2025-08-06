package com.clockwise.features.consumption.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BulkCreateConsumptionRecordDto(
    val workSessionId: String,
    val consumptions: List<ConsumptionItemUsageDto>
)