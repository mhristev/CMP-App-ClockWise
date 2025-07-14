package com.clockwise.features.clockin.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import com.clockwise.features.location.domain.model.BusinessUnitAddress

// Expected interface for platform-specific location service
expect class LocationService {
    suspend fun hasLocationPermission(): Boolean
    suspend fun requestLocationPermission(): Boolean
    suspend fun getCurrentLocation(): LocationResult
}

// Location result sealed class
sealed class LocationResult {
    data class Success(val latitude: Double, val longitude: Double) : LocationResult()
    object PermissionDenied : LocationResult()
    object LocationDisabled : LocationResult()
    data class Error(val message: String) : LocationResult()
}

// Simple data classes for the demo
data class ClockInEligibility(
    val isEligible: Boolean,
    val distance: Double?,
    val reason: String,
    val hasPermission: Boolean = true,
    val currentLatitude: Double? = null,
    val currentLongitude: Double? = null
)

data class ClockInUiState(
    val isCheckingLocation: Boolean = false,
    val isRequestingPermission: Boolean = false,
    val eligibility: ClockInEligibility? = null,
    val message: String? = "Tap Clock In to verify your location",
    val isError: Boolean = false,
    val locationPermissionStatus: LocationPermissionStatus = LocationPermissionStatus.Unknown
)

class ClockInViewModel(
    private val locationService: LocationService
) : ViewModel() {
    
    var uiState by mutableStateOf(ClockInUiState())
        private set
    
    // Business unit address - will be set when screen is initialized
    private var businessUnitAddress: BusinessUnitAddress? = null
    
    /**
     * Set the business unit address for location verification
     */
    fun setBusinessUnitAddress(address: BusinessUnitAddress) {
        businessUnitAddress = address
        println("ClockInViewModel: Business unit address set to: ${address.name} at ${address.latitude}, ${address.longitude}")
    }
    
    /**
     * Calculates distance between two GPS coordinates using Haversine formula
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0 // Earth's radius in meters
        
        val lat1Rad = kotlin.math.PI * lat1 / 180.0
        val lat2Rad = kotlin.math.PI * lat2 / 180.0
        val deltaLatRad = kotlin.math.PI * (lat2 - lat1) / 180.0
        val deltaLonRad = kotlin.math.PI * (lon2 - lon1) / 180.0
        
        val a = sin(deltaLatRad / 2) * sin(deltaLatRad / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(deltaLonRad / 2) * sin(deltaLonRad / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return earthRadius * c
    }
    
    fun checkLocationEligibility() {
        viewModelScope.launch {
            // Check if business unit address is set
            val businessAddress = businessUnitAddress
            if (businessAddress == null) {
                uiState = uiState.copy(
                    isCheckingLocation = false,
                    message = "Business unit location not configured",
                    isError = true
                )
                return@launch
            }
            
            uiState = uiState.copy(
                isCheckingLocation = true,
                isRequestingPermission = false,
                message = "Checking location permissions...",
                isError = false
            )
            
            try {
                // First check if we have location permission
                val hasPermission = locationService.hasLocationPermission()
                
                if (!hasPermission) {
                    uiState = uiState.copy(
                        isRequestingPermission = true,
                        message = "Requesting location permission..."
                    )
                    
                    val permissionGranted = locationService.requestLocationPermission()
                    
                    if (!permissionGranted) {
                        uiState = uiState.copy(
                            isCheckingLocation = false,
                            isRequestingPermission = false,
                            eligibility = ClockInEligibility(
                                isEligible = false,
                                distance = null,
                                reason = "Location permission is required to clock in",
                                hasPermission = false
                            ),
                            message = "Location permission denied. Please enable location access to clock in.",
                            isError = true
                        )
                        return@launch
                    }
                }
                
                uiState = uiState.copy(
                    isRequestingPermission = false,
                    message = "Getting your location..."
                )
                
                // Get current location
                when (val locationResult = locationService.getCurrentLocation()) {
                    is LocationResult.Success -> {
                        println("ClockInViewModel: Current location: ${locationResult.latitude}, ${locationResult.longitude}")
                        println("ClockInViewModel: Business location: ${businessAddress.latitude}, ${businessAddress.longitude}")
                        
                        val distance = calculateDistance(
                            locationResult.latitude, locationResult.longitude,
                            businessAddress.latitude, businessAddress.longitude
                        )
                        
                        println("ClockInViewModel: Distance calculated: ${distance}m, allowed radius: ${businessAddress.allowedRadius}m")
                        
                        val isEligible = distance <= businessAddress.allowedRadius
                        
                        val eligibility = ClockInEligibility(
                            isEligible = isEligible,
                            distance = distance,
                            reason = if (isEligible) {
                                "You are within ${businessAddress.allowedRadius}m of your workplace"
                            } else {
                                "You are ${distance.toInt()}m away. You must be within ${businessAddress.allowedRadius}m to clock in"
                            },
                            hasPermission = true,
                            currentLatitude = locationResult.latitude,
                            currentLongitude = locationResult.longitude
                        )
                        
                        uiState = uiState.copy(
                            isCheckingLocation = false,
                            eligibility = eligibility,
                            message = when {
                                eligibility.isEligible -> "You're at the workplace! You can clock in."
                                eligibility.distance != null -> "You're ${eligibility.distance.toInt()} meters away from workplace."
                                else -> eligibility.reason
                            }
                        )
                    }
                    
                    is LocationResult.PermissionDenied -> {
                        uiState = uiState.copy(
                            isCheckingLocation = false,
                            eligibility = ClockInEligibility(
                                isEligible = false,
                                distance = null,
                                reason = "Location permission denied",
                                hasPermission = false
                            ),
                            message = "Location permission denied. Please enable location access to clock in.",
                            isError = true
                        )
                    }
                    
                    is LocationResult.LocationDisabled -> {
                        uiState = uiState.copy(
                            isCheckingLocation = false,
                            eligibility = ClockInEligibility(
                                isEligible = false,
                                distance = null,
                                reason = "Location services disabled",
                                hasPermission = true
                            ),
                            message = "Location services are disabled. Please enable GPS to clock in.",
                            isError = true
                        )
                    }
                    
                    is LocationResult.Error -> {
                        uiState = uiState.copy(
                            isCheckingLocation = false,
                            eligibility = ClockInEligibility(
                                isEligible = false,
                                distance = null,
                                reason = "Unable to get location: ${locationResult.message}",
                                hasPermission = true
                            ),
                            message = "Error getting location: ${locationResult.message}",
                            isError = true
                        )
                    }
                }
                
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isCheckingLocation = false,
                    isRequestingPermission = false,
                    message = "Error checking location: ${e.message}",
                    isError = true
                )
            }
        }
    }
    
    /**
     * Called when location permission is granted by the user
     */
    fun onLocationPermissionGranted() {
        println("ClockInViewModel: Location permission granted")
        uiState = uiState.copy(
            locationPermissionStatus = LocationPermissionStatus.Granted,
            isRequestingPermission = false,
            message = "Location permission granted! Tap Clock In to verify your location.",
            isError = false
        )
    }
    
    /**
     * Called when location permission is denied by the user
     */
    fun onLocationPermissionDenied() {
        println("ClockInViewModel: Location permission denied")
        uiState = uiState.copy(
            locationPermissionStatus = LocationPermissionStatus.Denied,
            isRequestingPermission = false,
            message = "Location permission denied. Please enable location access in your device settings to use clock-in functionality.",
            isError = true
        )
    }
    
    /**
     * Check location permission status without requesting permission
     */
    fun checkLocationPermissionStatus() {
        viewModelScope.launch {
            try {
                println("ClockInViewModel: Checking location permission status...")
                val hasPermission = locationService.hasLocationPermission()
                println("ClockInViewModel: Location permission result: $hasPermission")
                
                uiState = uiState.copy(
                    locationPermissionStatus = if (hasPermission) LocationPermissionStatus.Granted else LocationPermissionStatus.RequiresRequest,
                    message = if (hasPermission) "Location permission granted! Ready to verify location." else "Location permission required for clock-in verification.",
                    isError = false
                )
                println("ClockInViewModel: Updated UI state with permission status: ${uiState.locationPermissionStatus}")
            } catch (e: Exception) {
                println("ClockInViewModel: Error checking location permission: ${e.message}")
                e.printStackTrace()
                uiState = uiState.copy(
                    locationPermissionStatus = LocationPermissionStatus.Unknown,
                    message = "Error checking location permission: ${e.message}",
                    isError = true
                )
            }
        }
    }
    
    /**
     * Request location permission
     */
    fun requestLocationPermission() {
        viewModelScope.launch {
            println("ClockInViewModel: Requesting location permission...")
            uiState = uiState.copy(
                isRequestingPermission = true,
                message = "Requesting location permission..."
            )
            
            try {
                val granted = locationService.requestLocationPermission()
                println("ClockInViewModel: Location permission request result: $granted")
                if (granted) {
                    onLocationPermissionGranted()
                } else {
                    onLocationPermissionDenied()
                }
            } catch (e: Exception) {
                println("ClockInViewModel: Error requesting location permission: ${e.message}")
                e.printStackTrace()
                uiState = uiState.copy(
                    isRequestingPermission = false,
                    locationPermissionStatus = LocationPermissionStatus.Unknown,
                    message = "Error requesting location permission: ${e.message}",
                    isError = true
                )
            }
        }
    }
    
    /**
     * Handle clock-in button click with location verification
     */
    fun clockInWithLocationCheck() {
        viewModelScope.launch {
            // First check if we already have location data
            if (uiState.eligibility?.isEligible == true) {
                // Already verified - proceed with clock in
                clockIn()
                return@launch
            }
            
            // Need to verify location first
            uiState = uiState.copy(
                message = "Verifying your location for clock-in...",
                isError = false
            )
            
            checkLocationEligibility()
        }
    }
    
    fun clockIn() {
        if (uiState.eligibility?.isEligible == true) {
            // TODO: Implement actual clock-in logic
            uiState = uiState.copy(
                message = "Successfully clocked in!",
                isError = false
            )
        }
    }
}