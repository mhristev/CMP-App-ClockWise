package com.clockwise.features.consumption.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Domain model for consumption items available at a business unit
 */
data class ConsumptionItem(
    val id: String,
    val name: String,
    val price: Double,
    val type: String,
    val businessUnitId: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

/**
 * Represents a selected consumption item with quantity
 */
data class SelectedConsumptionItem(
    val consumptionItem: ConsumptionItem,
    val quantity: Int = 1
) {
    val totalPrice: Double
        get() = consumptionItem.price * quantity
}

/**
 * Data class for consumption items grouped by type
 */
data class ConsumptionItemsByType(
    val type: String,
    val items: List<ConsumptionItem>
)