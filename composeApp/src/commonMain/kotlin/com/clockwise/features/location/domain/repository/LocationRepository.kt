package com.clockwise.features.location.domain.repository

import com.clockwise.features.location.domain.model.LocationResult
import com.clockwise.features.location.domain.model.LocationPermissionResult

interface LocationRepository {
    suspend fun getCurrentLocation(): LocationResult
    suspend fun hasLocationPermission(): Boolean
    suspend fun requestLocationPermission(): LocationPermissionResult
}
