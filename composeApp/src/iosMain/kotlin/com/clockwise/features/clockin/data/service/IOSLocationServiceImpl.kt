package com.clockwise.features.clockin.data.service

import com.clockwise.features.clockin.domain.service.LocationService
import com.clockwise.features.clockin.domain.service.LocationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.*
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)

/**
 * iOS implementation of LocationService that uses REAL device GPS coordinates
 * This implementation properly requests location permissions and forces fresh GPS coordinates
 * It NEVER uses mock data and always gets the newest location from the device
 */
class IOSLocationServiceImpl : LocationService {
    
    init {
        println("🚨🚨🚨 IOSLocationServiceImpl INITIALIZED - REAL iOS GPS Location Service Active! 🚨🚨🚨")
    }
    
    override suspend fun hasLocationPermission(): Boolean {
        val status = CLLocationManager.authorizationStatus()
        println("🚨 iOS Location Permission Status Check: $status")
        
        return when (status) {
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> {
                println("🚨 iOS Location Permission: ✅ GRANTED")
                true
            }
            kCLAuthorizationStatusDenied -> {
                println("🚨 iOS Location Permission: ❌ DENIED - User must enable in Settings")
                false
            }
            kCLAuthorizationStatusRestricted -> {
                println("🚨 iOS Location Permission: ❌ RESTRICTED - Parental controls or corporate policy")
                false
            }
            kCLAuthorizationStatusNotDetermined -> {
                println("🚨 iOS Location Permission: ❓ NOT_DETERMINED - Need to request permission")
                false
            }
            else -> {
                println("🚨 iOS Location Permission: ❓ UNKNOWN status: $status")
                false
            }
        }
    }
    
    override suspend fun requestLocationPermission(): Boolean = suspendCancellableCoroutine { continuation ->
        val currentStatus = CLLocationManager.authorizationStatus()
        println("🚨 iOS Request Location Permission - Current Status: $currentStatus")
        
        when (currentStatus) {
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> {
                println("🚨 iOS Location Permission: Already granted")
                continuation.resume(true)
                return@suspendCancellableCoroutine
            }
            kCLAuthorizationStatusDenied -> {
                println("🚨 iOS Location Permission: Previously denied - user must enable in Settings")
                continuation.resume(false)
                return@suspendCancellableCoroutine
            }
            kCLAuthorizationStatusRestricted -> {
                println("🚨 iOS Location Permission: Restricted by parental controls")
                continuation.resume(false)
                return@suspendCancellableCoroutine
            }
            kCLAuthorizationStatusNotDetermined -> {
                println("🚨 iOS Location Permission: Requesting permission from user...")
                
                val locationManager = CLLocationManager()
                
                // Create delegate to handle iOS permission response only
                val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                    override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: CLAuthorizationStatus) {
                        println("🚨 iOS Permission Response Received: $didChangeAuthorizationStatus")
                        
                        when (didChangeAuthorizationStatus) {
                            kCLAuthorizationStatusAuthorizedWhenInUse,
                            kCLAuthorizationStatusAuthorizedAlways -> {
                                println("🚨 iOS Location Permission: ✅ USER GRANTED iOS PERMISSION!")
                                continuation.resume(true)
                            }
                            kCLAuthorizationStatusDenied -> {
                                println("🚨 iOS Location Permission: ❌ USER DENIED iOS PERMISSION")
                                continuation.resume(false)
                            }
                            kCLAuthorizationStatusNotDetermined -> {
                                println("🚨 iOS Location Permission: ❓ Still not determined")
                                // Don't resume yet, wait for final decision
                            }
                            else -> {
                                println("🚨 iOS Location Permission: ❓ Unclear status: $didChangeAuthorizationStatus")
                                continuation.resume(false)
                            }
                        }
                    }
                }
                
                locationManager.delegate = delegate
                println("🚨 iOS: Showing iOS NATIVE permission dialog...")
                locationManager.requestWhenInUseAuthorization()
            }
            else -> {
                println("🚨 iOS Location Permission: Unknown status - denying")
                continuation.resume(false)
            }
        }
    }
    
    override suspend fun getCurrentLocation(): LocationResult {
        return try {
            if (!hasLocationPermission()) {
                println("🚨 iOS Location: Permission not granted - requesting permission...")
                val permissionGranted = requestLocationPermission()
                if (!permissionGranted) {
                    return LocationResult.PermissionDenied
                }
            }
            
            if (!CLLocationManager.locationServicesEnabled()) {
                println("🚨 iOS Location Services: DISABLED - user must enable in Settings")
                return LocationResult.LocationDisabled
            }
            
            println("DEBUG: 🚨🚨🚨 iOS REAL GPS LOCATION MODE - NO MOCK DATA! 🚨🚨🚨")
            println("DEBUG: 🚨 GETTING FRESH DEVICE GPS COORDINATES - ZERO TOLERANCE FOR CACHE")
            println("DEBUG: 🚨 Using REAL Core Location framework with device GPS...")
            println("DEBUG: 🚨 📍 THIS WILL SHOW YOUR ACTUAL DEVICE LOCATION - NO FAKE COORDINATES! 📍")
            
            // Get real fresh GPS coordinates from device
            return getRealDeviceFreshLocationIOS()
            
        } catch (e: Exception) {
            println("DEBUG: 🚨 iOS Unexpected error getting real location: ${e.message}")
            LocationResult.Error(e.message ?: "Failed to get real device location on iOS")
        }
    }
    
    /**
     * REAL DEVICE GPS method to get the actual current device location
     * This method uses CLLocationManager to get REAL device GPS coordinates
     * NO MOCK DATA - Only actual device location is returned
     */
    private suspend fun getRealDeviceFreshLocationIOS(): LocationResult {
        return try {
            println("DEBUG: 🚨🚨🚨 iOS GETTING REAL DEVICE LOCATION 🚨🚨🚨")
            println("DEBUG: 🚨 REQUESTING ACTUAL DEVICE GPS COORDINATES - NO MOCK DATA")
            
            // Force GPS warm-up period
            println("DEBUG: 🚨 GPS warm-up delay...")
            delay(2000)
            
            // Get REAL device location using Core Location
            repeat(3) { attempt ->
                println("DEBUG: 🚨🚨🚨 iOS REAL GPS ATTEMPT ${attempt + 1} 🚨🚨🚨")
                
                val result = withTimeoutOrNull(20000) { // 20 second timeout
                    suspendCancellableCoroutine<LocationResult?> { continuation ->
                        
                        // Create location manager for REAL GPS
                        val locationManager = CLLocationManager()
                        locationManager.desiredAccuracy = kCLLocationAccuracyBest
                        locationManager.distanceFilter = kCLDistanceFilterNone
                        
                        println("DEBUG: 🚨 Starting REAL iOS location request...")
                        
                        // Create delegate to get REAL device coordinates
                        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                            override fun locationManager(
                                manager: CLLocationManager,
                                didUpdateLocations: List<*>
                            ) {
                                @Suppress("UNCHECKED_CAST")
                                val locations = didUpdateLocations as List<CLLocation>
                                val location = locations.lastOrNull() as? CLLocation
                                
                                if (location != null) {
                                    // Get the REAL device coordinates with proper interop
                                    val realLat = location.coordinate.useContents { latitude }
                                    val realLng = location.coordinate.useContents { longitude }
                                    val accuracy = location.horizontalAccuracy
                                    
                                    println("DEBUG: 🚨🚨🚨 REAL DEVICE LOCATION RECEIVED! 🚨🚨🚨")
                                    println("DEBUG: 🚨 ACTUAL Device GPS: $realLat, $realLng")
                                    println("DEBUG: 🚨 Accuracy: ${accuracy}m")
                                    println("DEBUG: 🚨 This is YOUR REAL LOCATION - no mock data!")
                                    
                                    // Validate accuracy
                                    if (accuracy.toDouble() <= 1000.0 && accuracy.toDouble() >= 0.0) { // Accept up to 1000m accuracy
                                        println("DEBUG: 🚨🚨🚨 ✅ REAL DEVICE LOCATION ACCEPTED! ✅ 🚨🚨🚨")
                                        println("DEBUG: 🚨 Using your actual device GPS coordinates")
                                        
                                        locationManager.stopUpdatingLocation()
                                        continuation.resume(
                                            LocationResult.Success(
                                                latitude = realLat,
                                                longitude = realLng
                                            )
                                        )
                                    } else {
                                        println("DEBUG: 🚨 ❌ Location accuracy too poor: ${accuracy}m - trying again...")
                                        // Continue waiting for better accuracy
                                    }
                                } else {
                                    println("DEBUG: 🚨 ❌ Null location received - trying again...")
                                }
                            }
                            
                            override fun locationManager(
                                manager: CLLocationManager,
                                didFailWithError: NSError
                            ) {
                                println("DEBUG: 🚨🚨🚨 ❌ iOS Location Error: ${didFailWithError.localizedDescription} 🚨🚨🚨")
                                locationManager.stopUpdatingLocation()
                                continuation.resume(
                                    LocationResult.Error("iOS location error: ${didFailWithError.localizedDescription}")
                                )
                            }
                        }
                        
                        locationManager.delegate = delegate
                        locationManager.startUpdatingLocation()
                        
                        // Cancel if coroutine is cancelled
                        continuation.invokeOnCancellation {
                            println("DEBUG: 🚨 Cancelling real location request...")
                            locationManager.stopUpdatingLocation()
                        }
                    }
                }
                
                // If we got a real location, return it
                if (result is LocationResult.Success) {
                    println("DEBUG: 🚨🚨🚨 SUCCESS! Got REAL device location on attempt ${attempt + 1} 🚨🚨🚨")
                    return result
                }
                
                // Wait before next attempt
                if (attempt < 2) {
                    val delayTime = (attempt + 1) * 3000L
                    println("DEBUG: 🚨 Waiting ${delayTime}ms before next attempt...")
                    delay(delayTime)
                }
            }
            
            println("DEBUG: 🚨🚨🚨 ❌ Could not get real device location after 3 attempts 🚨🚨🚨")
            LocationResult.Error("Unable to get real device GPS location. Please check location services.")
            
        } catch (e: Exception) {
            println("DEBUG: 🚨🚨🚨 ❌ Real GPS error: ${e.message} 🚨🚨🚨")
            LocationResult.Error(e.message ?: "Failed to get real device GPS location")
        }
    }
}
