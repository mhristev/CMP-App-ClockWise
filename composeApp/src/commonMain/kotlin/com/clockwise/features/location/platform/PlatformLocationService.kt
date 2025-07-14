package com.clockwise.features.location.platform

import com.clockwise.features.location.domain.model.Location
import com.clockwise.features.location.domain.model.LocationResult
import com.clockwise.features.location.domain.model.LocationPermissionResult
import kotlinx.coroutines.flow.Flow

interface PlatformLocationService {
    suspend fun requestLocationPermission(): LocationPermissionResult
    suspend fun hasLocationPermission(): Boolean
    suspend fun getCurrentLocation(): LocationResult
    fun trackLocationUpdates(): Flow<LocationResult>
    fun stopLocationTracking()
}
