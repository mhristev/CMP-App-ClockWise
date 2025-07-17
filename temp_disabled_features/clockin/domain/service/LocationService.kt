package com.clockwise.features.clockin.domain.service

/**
 * Service for handling location operations
 */
interface LocationService {
    suspend fun hasLocationPermission(): Boolean
    suspend fun requestLocationPermission(): Boolean
    suspend fun getCurrentLocation(): LocationResult
}

/**
 * Sealed class for location results
 */
sealed class LocationResult {
    data class Success(val latitude: Double, val longitude: Double) : LocationResult()
    data class Error(val message: String) : LocationResult()
    object PermissionDenied : LocationResult()
    object LocationUnavailable : LocationResult()
    object LocationDisabled : LocationResult()
}
