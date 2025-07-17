package com.clockwise.features.location.domain.model

sealed class ClockInEligibility {
    object Eligible : ClockInEligibility()
    data class TooFar(val distance: Double, val allowedRadius: Double) : ClockInEligibility()
    object LocationPermissionRequired : ClockInEligibility()
    object LocationServicesDisabled : ClockInEligibility()
    data class LocationError(val message: String) : ClockInEligibility()
}
