package com.clockwise.features.clockin.domain.model

/**
 * Data class representing clock-in eligibility status
 */
data class ClockInEligibility(
    val isEligible: Boolean,
    val distance: Double? = null,
    val distanceMessage: String = "",
    val reason: String = ""
)
