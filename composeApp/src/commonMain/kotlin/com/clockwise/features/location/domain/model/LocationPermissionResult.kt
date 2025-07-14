package com.clockwise.features.location.domain.model

/**
 * Sealed class representing location permission status
 */
sealed class LocationPermissionResult {
    object Granted : LocationPermissionResult()
    object Denied : LocationPermissionResult()
    object PermanentlyDenied : LocationPermissionResult()
}
