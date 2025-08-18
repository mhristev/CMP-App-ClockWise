package com.clockwise.features.location.domain.usecase

import com.clockwise.features.organization.data.model.BusinessUnitAddress
import com.clockwise.features.location.domain.model.ClockInEligibility
import com.clockwise.features.location.domain.model.Location
import com.clockwise.features.location.domain.model.LocationResult
import com.clockwise.features.location.domain.model.toLocation
import com.clockwise.features.location.domain.repository.LocationRepository
import kotlin.math.*

/**
 * Use case for checking if a user is eligible to clock in based on their location
 */
class CheckClockInEligibilityUseCase(
    private val locationRepository: LocationRepository
) {
    
    /**
     * Calculates the distance between two locations using the Haversine formula
     */
    private fun calculateDistance(location1: Location, location2: Location): Double {
        val earthRadius = 6371000.0 // Earth's radius in meters
        
        val lat1Rad = (location1.latitude * PI) / 180.0
        val lat2Rad = (location2.latitude * PI) / 180.0
        val deltaLatRad = ((location2.latitude - location1.latitude) * PI) / 180.0
        val deltaLonRad = ((location2.longitude - location1.longitude) * PI) / 180.0
        
        val a = sin(deltaLatRad / 2) * sin(deltaLatRad / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(deltaLonRad / 2) * sin(deltaLonRad / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return earthRadius * c
    }
    
    suspend operator fun invoke(businessUnitAddress: BusinessUnitAddress): ClockInEligibility {
        // First check if we have location permission
        if (!locationRepository.hasLocationPermission()) {
            return ClockInEligibility(
                isEligible = false,
                userLocation = null,
                businessLocation = businessUnitAddress,
                distance = null,
                reason = "Location permission is required to clock in"
            )
        }
        
        // Get current location
        return when (val locationResult = locationRepository.getCurrentLocation()) {
            is LocationResult.Success -> {
                val userLocation = locationResult.location
                val businessLocation = businessUnitAddress.toLocation()
                
                if (businessLocation == null) {
                    // Business unit doesn't have location coordinates set up
                    return ClockInEligibility(
                        isEligible = false,
                        userLocation = userLocation,
                        businessLocation = businessUnitAddress,
                        distance = null,
                        reason = "Business unit location is not configured. Please contact your manager to set up location coordinates."
                    )
                }
                
                val distance = calculateDistance(userLocation, businessLocation)
                
                val isWithinRadius = distance <= businessUnitAddress.allowedRadius
                
                ClockInEligibility(
                    isEligible = isWithinRadius,
                    userLocation = userLocation,
                    businessLocation = businessUnitAddress,
                    distance = distance,
                    reason = if (isWithinRadius) {
                        "You are within ${businessUnitAddress.allowedRadius}m of your workplace"
                    } else {
                        "You are ${distance.toInt()}m away. You must be within ${businessUnitAddress.allowedRadius}m to clock in"
                    }
                )
            }
            
            is LocationResult.PermissionDenied -> {
                ClockInEligibility(
                    isEligible = false,
                    userLocation = null,
                    businessLocation = businessUnitAddress,
                    distance = null,
                    reason = "Location permission is denied. Please enable location access to clock in"
                )
            }
            
            is LocationResult.LocationDisabled -> {
                ClockInEligibility(
                    isEligible = false,
                    userLocation = null,
                    businessLocation = businessUnitAddress,
                    distance = null,
                    reason = "Location services are disabled. Please enable GPS to clock in"
                )
            }
            
            is LocationResult.Error -> {
                ClockInEligibility(
                    isEligible = false,
                    userLocation = null,
                    businessLocation = businessUnitAddress,
                    distance = null,
                    reason = "Unable to get your location: ${locationResult.message}"
                )
            }
            
            is LocationResult.Loading -> {
                ClockInEligibility(
                    isEligible = false,
                    userLocation = null,
                    businessLocation = businessUnitAddress,
                    distance = null,
                    reason = "Getting your location..."
                )
            }
        }
    }
}
