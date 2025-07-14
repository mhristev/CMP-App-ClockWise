package com.clockwise.features.location.domain.model

/**
 * Represents a business unit address with GPS coordinates
 */
data class BusinessUnitAddress(
    val businessUnitId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val allowedRadius: Double = 100.0 // Default 100 meters
) {
    fun toLocation(): Location {
        return Location(
            latitude = latitude,
            longitude = longitude,
            accuracy = 0f,
            timestamp = System.currentTimeMillis()
        )
    }
}
