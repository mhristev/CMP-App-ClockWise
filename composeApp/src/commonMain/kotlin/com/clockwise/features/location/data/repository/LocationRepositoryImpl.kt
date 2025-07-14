package com.clockwise.features.location.data.repository

import com.clockwise.features.location.platform.PlatformLocationService
import com.clockwise.features.location.domain.model.LocationPermissionResult
import com.clockwise.features.location.domain.model.LocationResult
import com.clockwise.features.location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of LocationRepository that delegates to platform-specific location service
 */
class LocationRepositoryImpl(
    private val platformLocationService: PlatformLocationService
) : LocationRepository {
    
    override suspend fun requestLocationPermission(): LocationPermissionResult {
        return platformLocationService.requestLocationPermission()
    }
    
    override suspend fun hasLocationPermission(): Boolean {
        return platformLocationService.hasLocationPermission()
    }
    
    override suspend fun getCurrentLocation(): LocationResult {
        return platformLocationService.getCurrentLocation()
    }
    
    override fun trackLocationUpdates(): Flow<LocationResult> {
        return platformLocationService.trackLocationUpdates()
    }
    
    override suspend fun stopLocationTracking() {
        platformLocationService.stopLocationTracking()
    }
}