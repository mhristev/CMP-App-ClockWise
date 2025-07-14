package com.clockwise.features.location.domain.repository

import com.clockwise.features.location.domain.model.LocationPermissionResult
import com.clockwise.features.location.domain.model.LocationResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for location operations
 */
interface LocationRepository {
    
    /**
     * Requests location permission from the user
     */
    suspend fun requestLocationPermission(): LocationPermissionResult
    
    /**
     * Checks if location permission is granted
     */
    suspend fun hasLocationPermission(): Boolean
    
    /**
     * Gets the current location once
     */
    suspend fun getCurrentLocation(): LocationResult
    
    /**
     * Starts tracking location updates
     */
    fun trackLocationUpdates(): Flow<LocationResult>
    
    /**
     * Stops location tracking
     */
    suspend fun stopLocationTracking()
}