package com.clockwise.features.clockin.data.service

import com.clockwise.features.clockin.domain.service.LocationService
import com.clockwise.features.clockin.domain.service.LocationResult

/**
 * Mock implementation of LocationService for development and testing
 */
class MockLocationService : LocationService {
    
    override suspend fun hasLocationPermission(): Boolean {
        // For mock purposes, assume permission is granted
        return true
    }
    
    override suspend fun requestLocationPermission(): Boolean {
        // For mock purposes, assume permission is granted
        return true
    }
    
    override suspend fun getCurrentLocation(): LocationResult {
        // Return mock location (Eindhoven, Netherlands area)
        return LocationResult.Success(
            latitude = 51.4381, 
            longitude = 5.4752
        )
    }
}
