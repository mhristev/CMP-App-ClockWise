package com.clockwise.features.location.domain.model

/**
 * Sealed class representing the result of a location operation
 */
sealed class LocationResult {
    data class Success(val location: Location) : LocationResult()
    object PermissionDenied : LocationResult()
    object LocationDisabled : LocationResult()
    data class Error(val message: String) : LocationResult()
}
