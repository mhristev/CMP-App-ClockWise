package com.clockwise.features.location.data.repository

import com.clockwise.features.location.domain.model.Location
import com.clockwise.features.location.domain.model.LocationPermissionResult
import com.clockwise.features.location.domain.model.LocationResult
import com.clockwise.features.location.domain.repository.LocationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Mock implementation of LocationRepository for testing
 */
class MockLocationRepository : LocationRepository {
    
    override suspend fun requestLocationPermission(): LocationPermissionResult {
        return LocationPermissionResult.Granted
    }
    
    override suspend fun hasLocationPermission(): Boolean {
        return true
    }
    
    override suspend fun getCurrentLocation(): LocationResult {
        // Simulate network delay
        delay(1000)
        
        // Return mock Boston coordinates
        return LocationResult.Success(
            Location(
                latitude = 42.3601,
                longitude = -71.0589,
                accuracy = 5.0f,
                timestamp = System.currentTimeMillis()
            )
        )
    }
    
    override fun trackLocationUpdates(): Flow<LocationResult> {
        return flowOf(
            LocationResult.Success(
                Location(
                    latitude = 42.3601,
                    longitude = -71.0589,
                    accuracy = 5.0f,
                    timestamp = System.currentTimeMillis()
                )
            )
        )
    }
    
    override suspend fun stopLocationTracking() {
        // Mock implementation - do nothing
    }
}
