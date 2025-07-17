package com.clockwise.features.location.domain.model

data class BusinessUnitAddress(
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val allowedRadius: Double = 100.0 // Default 100 meters
) {
    fun toLocation(): Location {
        return Location(
            latitude = latitude,
            longitude = longitude
        )
    }
}
