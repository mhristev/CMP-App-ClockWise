package com.clockwise.features.location.data.platform

import com.clockwise.features.clockin.domain.model.LocationResult
import kotlinx.coroutines.flow.Flow

/**
 * Platform-specific interface for location services.
 * This will be implemented differently for Android and iOS.
 * Uses the existing clockin domain models for consistency.
 */
interface PlatformLocationService {
    
    /**
     * Requests location permission from the user
     */
    suspend fun requestLocationPermission(): Boolean
    
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
