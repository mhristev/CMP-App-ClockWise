package com.clockwise.features.consumption.data.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsumptionItemDto(
    @SerialName("id")
    val id: String,
    
    @SerialName("name")
    val name: String,
    
    @SerialName("price")
    val price: Double,
    
    @SerialName("type")
    val type: String,
    
    @SerialName("business_unit_id")
    val businessUnitId: String,
    
    @SerialName("created_at")
    val createdAt: LocalDateTime,
    
    @SerialName("updated_at")
    val updatedAt: LocalDateTime
)