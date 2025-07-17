package com.clockwise.features.location.domain.model

import com.clockwise.features.organization.data.model.BusinessUnitAddress

data class ClockInEligibility(
    val isEligible: Boolean,
    val userLocation: Location?,
    val businessLocation: BusinessUnitAddress,
    val distance: Double?,
    val reason: String
)