package com.clockwise.features.clockin.domain.model

/**
 * Sealed class representing location permission results
 */
sealed class LocationPermissionResult {
    object Granted : LocationPermissionResult()
    object Denied : LocationPermissionResult()
    object DeniedPermanently : LocationPermissionResult()
}
