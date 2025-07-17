package com.clockwise.features.clockin.domain.repository

import com.clockwise.features.clockin.domain.model.LocationResult
import com.clockwise.features.clockin.domain.model.LocationPermissionResult

/**
 * Repository interface for location operations
 */
interface LocationRepository {
    suspend fun hasLocationPermission(): Boolean
    suspend fun requestLocationPermission(): LocationPermissionResult
    suspend fun getCurrentLocation(): LocationResult
}
