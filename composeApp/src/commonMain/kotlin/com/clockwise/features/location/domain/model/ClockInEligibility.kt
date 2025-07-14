package com.clockwise.features.location.domain.model

/**
 * Represents the eligibility status for clocking in based on location
 */
data class ClockInEligibility(
    val isEligible: Boolean,
    val userLocation: Location?,
    val businessLocation: BusinessUnitAddress?,
    val distance: Double?,
    val reason: String
)
