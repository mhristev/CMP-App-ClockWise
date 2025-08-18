package com.clockwise.features.organization.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BusinessUnitAddressDto(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double?,
    val longitude: Double?,
    val allowedRadius: Double = 200.0 // Default 200m radius
)

// Enhanced DTO for full business unit information from /v1/business-units/{id}
@Serializable
data class BusinessUnitDto(
    val id: String,
    val name: String,
    val location: String,
    val description: String?,
    val companyId: String,
    val latitude: Double?,
    val longitude: Double?,
    val allowedRadius: Double = 200.0,
    val phoneNumber: String?,
    val email: String?
)

// Domain model for use in the app
data class BusinessUnitAddress(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double?,
    val longitude: Double?,
    val allowedRadius: Double = 200.0
)

// Enhanced domain model for business unit dashboard
data class BusinessUnit(
    val id: String,
    val name: String,
    val location: String,
    val description: String?,
    val companyId: String,
    val latitude: Double?,
    val longitude: Double?,
    val allowedRadius: Double = 200.0,
    val phoneNumber: String?,
    val email: String?
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

// Extension function to convert enhanced DTO to domain model
fun BusinessUnitDto.toDomain(): BusinessUnit {
    return BusinessUnit(
        id = id,
        name = name,
        location = location,
        description = description,
        companyId = companyId,
        latitude = latitude,
        longitude = longitude,
        allowedRadius = allowedRadius,
        phoneNumber = phoneNumber,
        email = email
    )
}
