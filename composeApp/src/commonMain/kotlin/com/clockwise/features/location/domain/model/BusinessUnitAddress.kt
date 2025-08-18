package com.clockwise.features.location.domain.model

import com.clockwise.features.organization.data.model.BusinessUnitAddress

/**
 * Extension function to convert BusinessUnitAddress to Location
 * Returns null if latitude or longitude are null
 */
fun BusinessUnitAddress.toLocation(): Location? {
    val lat = latitude
    val lng = longitude
    
    return if (lat != null && lng != null) {
        Location(
            latitude = lat,
            longitude = lng
        )
    } else {
        null
    }
}