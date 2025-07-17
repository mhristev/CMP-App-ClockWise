package com.clockwise.features.location.domain.model

import com.clockwise.features.organization.data.model.BusinessUnitAddress

/**
 * Extension function to convert BusinessUnitAddress to Location
 */
fun BusinessUnitAddress.toLocation(): Location {
    return Location(
        latitude = latitude,
        longitude = longitude
    )
}