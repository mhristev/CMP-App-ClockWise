package com.clockwise.features.organization.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BusinessUnitAddressDto(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val allowedRadius: Double = 200.0 // Default 200m radius
)

// Domain model for use in the app
data class BusinessUnitAddress(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val allowedRadius: Double = 200.0
)

// Extension function to convert DTO to domain model
fun BusinessUnitAddressDto.toDomain(): BusinessUnitAddress {
    return BusinessUnitAddress(
        id = id,
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        allowedRadius = allowedRadius
    )
}
