package com.clockwise.features.clockin.data.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.clockwise.features.clockin.domain.service.LocationService
import com.clockwise.features.clockin.domain.service.LocationResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/**
 * Android implementation of LocationService that uses Google Play Services
 * for accurate GPS location data instead of mock coordinates
 */
class AndroidLocationServiceImpl(
    private val context: Context
) : LocationService {
    
    init {
        println("🚨🚨🚨 AndroidLocationServiceImpl INITIALIZED - Real GPS service active! 🚨🚨🚨")
    }
    
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    override suspend fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    override suspend fun requestLocationPermission(): Boolean {
        // On Android, permission requests must be handled by the UI layer
        // This method just checks current permission status
        // The actual permission request is handled by LocationPermissionHandler in the UI
        return hasLocationPermission()
    }
     override suspend fun getCurrentLocation(): LocationResult {
        return try {
            if (!hasLocationPermission()) {
                return LocationResult.PermissionDenied
            }
            
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && 
                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                return LocationResult.LocationDisabled
            }
            
            println("DEBUG: 🚨🚨🚨 ZERO-TOLERANCE FRESH LOCATION MODE 🚨🚨🚨")
            println("DEBUG: 🚨 FORCING COMPLETELY FRESH LOCATION - ABSOLUTELY NO CACHE ALLOWED")
            println("DEBUG: 🚨 Clearing ALL potential caches before requesting new location...")
            
            // Clear any potential cached location data first
            try {
                fusedLocationClient.flushLocations()
                println("DEBUG: 🚨 Flushed FusedLocationClient cache")
            } catch (e: Exception) {
                println("DEBUG: 🚨 Cache flush attempt: ${e.message}")
            }
            
            // ALWAYS use the zero-tolerance method which forces completely fresh location
            return getZeroToleranceFreshLocation()
            
        } catch (e: SecurityException) {
            println("DEBUG: Location permission denied during request")
            LocationResult.PermissionDenied
        } catch (e: Exception) {
            println("DEBUG: Unexpected error getting location: ${e.message}")
            LocationResult.Error(e.message ?: "Failed to get location")
        }
    }

    /**
     * ZERO-TOLERANCE method to force completely fresh location using location updates
     * This method ensures we NEVER EVER get cached location data by being extremely aggressive
     * and using only the most recent GPS fix possible
     */
    private suspend fun getZeroToleranceFreshLocation(): LocationResult {
        return try {
            println("DEBUG: 🚨🚨🚨 ZERO-TOLERANCE FRESH LOCATION MODE ACTIVATED 🚨🚨🚨")
            println("DEBUG: 🚨 DEMANDING ABSOLUTELY FRESH GPS COORDINATES - ZERO TOLERANCE FOR CACHE")
            
            // Clear ALL potential location caches
            try {
                println("DEBUG: 🚨 Attempting to clear LocationManager cache...")
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                // Force clear any cached data
                fusedLocationClient.flushLocations()
                
                // Remove any existing location listeners to force fresh requests
                try {
                    fusedLocationClient.removeLocationUpdates { }
                } catch (e: Exception) {
                    // Ignore - just making sure no old listeners are active
                }
                
                // Try to trigger cache clearing
                fusedLocationClient.lastLocation.addOnCompleteListener { 
                    println("DEBUG: 🚨 Triggered lastLocation cache clearing")
                }
                
                println("DEBUG: 🚨 Cache clearing attempts completed")
            } catch (e: Exception) {
                println("DEBUG: 🚨 Cache clearing: ${e.message}")
            }
            
            // Wait for GPS to completely reset and warm up
            println("DEBUG: 🚨 Forcing GPS reset and 3-second warm-up...")
            delay(3000)
            
            // Strategy: Only accept location that is FRESH from GPS within 1 second
            repeat(5) { attempt ->
                println("DEBUG: 🚨🚨🚨 ZERO-TOLERANCE ATTEMPT ${attempt + 1} 🚨🚨🚨")
                
                val result = withTimeoutOrNull(15000) { // 15 second timeout per attempt
                    suspendCancellableCoroutine<LocationResult?> { continuation ->
                        val requestTime = System.currentTimeMillis()
                        
                        // Use MOST AGGRESSIVE settings possible to force completely fresh GPS
                        val intervalMs = 25L // Super aggressive: 25ms intervals
                        
                        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
                            .setMinUpdateIntervalMillis(intervalMs) // Force immediate updates
                            .setMaxUpdates(1) // Stop after FIRST fresh update
                            .setWaitForAccurateLocation(true) // Wait for accurate GPS fix
                            .setMinUpdateDistanceMeters(0f) // Accept ANY movement
                            .setMaxUpdateDelayMillis(intervalMs) // Force immediate delivery
                            .build()

                        val locationCallback = object : LocationCallback() {
                            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                                val location = locationResult.lastLocation
                                if (location != null) {
                                    val locationAge = System.currentTimeMillis() - location.time
                                    val totalRequestTime = System.currentTimeMillis() - requestTime
                                    
                                    println("DEBUG: 🚨🚨🚨 ZERO-TOLERANCE LOCATION CHECK (Attempt ${attempt + 1}) 🚨🚨🚨")
                                    println("DEBUG: 🚨 Location: ${location.latitude}, ${location.longitude}")
                                    println("DEBUG: 🚨 Accuracy: ${location.accuracy}m")
                                    println("DEBUG: 🚨 Location timestamp: ${location.time}")
                                    println("DEBUG: 🚨 Request started: $requestTime")
                                    println("DEBUG: 🚨 Current time: ${System.currentTimeMillis()}")
                                    println("DEBUG: 🚨 Location age: ${locationAge}ms (${locationAge / 1000.0}s)")
                                    println("DEBUG: 🚨 Total request time: ${totalRequestTime}ms")
                                    println("DEBUG: 🚨 Provider: ${location.provider}")
                                    
                                    // ZERO TOLERANCE: Only accept location that is LESS THAN 1 SECOND OLD
                                    val maxAge = 1000L // Absolutely maximum 1 second old
                                    
                                    println("DEBUG: 🚨 ZERO-TOLERANCE THRESHOLD: ${maxAge}ms")
                                    println("DEBUG: 🚨 Is fresh enough: ${locationAge <= maxAge}")
                                    
                                    if (locationAge <= maxAge) {
                                        println("DEBUG: 🚨🚨🚨 ✅ ZERO-TOLERANCE LOCATION ACCEPTED! ✅ 🚨🚨🚨")
                                        println("DEBUG: 🚨 FRESH GPS COORDINATES CONFIRMED!")
                                        println("DEBUG: 🚨🚨🚨 =============================================== 🚨🚨🚨")
                                        
                                        fusedLocationClient.removeLocationUpdates(this)
                                        continuation.resume(
                                            LocationResult.Success(
                                                latitude = location.latitude,
                                                longitude = location.longitude
                                            )
                                        )
                                    } else {
                                        println("DEBUG: 🚨 ❌ REJECTED: Location too stale (${locationAge}ms) - DEMANDING FRESHER!")
                                        fusedLocationClient.removeLocationUpdates(this)
                                        continuation.resume(null) // Try again with fresh request
                                    }
                                } else {
                                    println("DEBUG: 🚨 ❌ REJECTED: Null location received - trying again...")
                                    fusedLocationClient.removeLocationUpdates(this)
                                    continuation.resume(null) // Try again
                                }
                            }
                        }

                        println("DEBUG: 🚨 Starting ZERO-TOLERANCE location updates with ${intervalMs}ms intervals...")
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )

                        // Cancel location updates if coroutine is cancelled
                        continuation.invokeOnCancellation {
                            println("DEBUG: 🚨 Cancelling zero-tolerance location updates...")
                            fusedLocationClient.removeLocationUpdates(locationCallback)
                        }
                    }
                }
                
                // If we got a fresh result, return it immediately
                if (result is LocationResult.Success) {
                    println("DEBUG: 🚨🚨🚨 SUCCESS! Got ZERO-TOLERANCE fresh location on attempt ${attempt + 1} 🚨🚨🚨")
                    return result
                }
                
                // Longer delay between attempts to let GPS completely reset
                if (attempt < 4) {
                    val delayTime = (attempt + 1) * 3000L // 3s, 6s, 9s, 12s delay
                    println("DEBUG: 🚨 ZERO-TOLERANCE: Waiting ${delayTime}ms before next attempt (GPS reset time)...")
                    delay(delayTime)
                }
            }
            
            println("DEBUG: 🚨🚨🚨 ❌ ALL ZERO-TOLERANCE ATTEMPTS FAILED 🚨🚨🚨")
            LocationResult.Error("Unable to get absolutely fresh location after multiple zero-tolerance attempts. GPS may be unavailable or location services may be restricted.")
            
        } catch (e: Exception) {
            println("DEBUG: 🚨🚨🚨 ❌ ZERO-TOLERANCE location failed: ${e.message} 🚨🚨🚨")
            LocationResult.Error(e.message ?: "Failed to get zero-tolerance fresh location")
        }
    }
}
