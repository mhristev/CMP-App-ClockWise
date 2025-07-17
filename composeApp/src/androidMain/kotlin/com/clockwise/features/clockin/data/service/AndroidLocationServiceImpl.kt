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
            
            println("DEBUG: 🚨 FORCING COMPLETELY FRESH LOCATION - NO CACHE ALLOWED")
            
            // ALWAYS use the backup method which forces fresh location
            // Skip the regular getCurrentLocation() as it may return cached data
            return getFreshLocationWithUpdates()
            
        } catch (e: SecurityException) {
            println("DEBUG: Location permission denied during request")
            LocationResult.PermissionDenied
        } catch (e: Exception) {
            println("DEBUG: Unexpected error getting location: ${e.message}")
            LocationResult.Error(e.message ?: "Failed to get location")
        }
    }
    
    /**
     * Aggressive method to force completely fresh location using location updates
     * This method ensures we NEVER get cached location data
     */
    private suspend fun getFreshLocationWithUpdates(): LocationResult {
        return try {
            println("DEBUG: 🎯 ULTRA-FRESH LOCATION MODE ACTIVATED")
            println("DEBUG: 🎯 Clearing any potential location cache...")
            
            // Strategy 1: Try multiple rapid location requests to force GPS refresh
            repeat(3) { attempt ->
                println("DEBUG: 🎯 Attempt ${attempt + 1}: Requesting fresh location...")
                
                val result = withTimeoutOrNull(8000) { // 8 second timeout per attempt
                    suspendCancellableCoroutine<LocationResult?> { continuation ->
                        val requestTime = System.currentTimeMillis()
                        
                        // Use very aggressive settings to force fresh GPS
                        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                            .setMinUpdateIntervalMillis(50) // Super frequent updates
                            .setMaxUpdates(1) // Stop after first update
                            .setWaitForAccurateLocation(true) // Wait for accurate GPS fix
                            .setMinUpdateDistanceMeters(0f) // Accept any distance change
                            .build()

                        val locationCallback = object : LocationCallback() {
                            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                                val location = locationResult.lastLocation
                                if (location != null) {
                                    val locationAge = System.currentTimeMillis() - location.time
                                    val totalRequestTime = System.currentTimeMillis() - requestTime
                                    
                                    println("DEBUG: 🎯 ========== ULTRA-FRESH LOCATION (Attempt ${attempt + 1}) ==========")
                                    println("DEBUG: 🎯 Location: ${location.latitude}, ${location.longitude}")
                                    println("DEBUG: 🎯 Accuracy: ${location.accuracy}m")
                                    println("DEBUG: 🎯 Location timestamp: ${location.time}")
                                    println("DEBUG: 🎯 Request started: $requestTime")
                                    println("DEBUG: 🎯 Location age: ${locationAge}ms (${locationAge / 1000}s)")
                                    println("DEBUG: 🎯 Total request time: ${totalRequestTime}ms")
                                    println("DEBUG: 🎯 Provider: ${location.provider}")
                                    println("DEBUG: 🎯 Is ULTRA-FRESH: ${locationAge <= 3000}")
                                    
                                    // Only accept VERY fresh location (less than 3 seconds old)
                                    if (locationAge <= 3000) {
                                        println("DEBUG: 🎯 ✅ ULTRA-FRESH LOCATION ACCEPTED!")
                                        println("DEBUG: 🎯 ================================================")
                                        
                                        fusedLocationClient.removeLocationUpdates(this)
                                        continuation.resume(
                                            LocationResult.Success(
                                                latitude = location.latitude,
                                                longitude = location.longitude
                                            )
                                        )
                                    } else {
                                        println("DEBUG: 🎯 ⚠️ Location still stale (${locationAge}ms), trying again...")
                                        fusedLocationClient.removeLocationUpdates(this)
                                        continuation.resume(null) // Try again
                                    }
                                }
                            }
                        }

                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )

                        // Cancel location updates if coroutine is cancelled
                        continuation.invokeOnCancellation {
                            fusedLocationClient.removeLocationUpdates(locationCallback)
                        }
                    }
                }
                
                // If we got a fresh result, return it immediately
                if (result is LocationResult.Success) {
                    println("DEBUG: 🎯 SUCCESS! Got ultra-fresh location on attempt ${attempt + 1}")
                    return result
                }
                
                // Small delay between attempts to let GPS settle
                if (attempt < 2) {
                    println("DEBUG: 🎯 Waiting 1 second before next attempt...")
                    kotlinx.coroutines.delay(1000)
                }
            }
            
            println("DEBUG: 🎯 ❌ All attempts failed to get ultra-fresh location")
            LocationResult.Error("Unable to get fresh location after multiple attempts. Please ensure GPS is enabled and try again in an area with good signal.")
            
        } catch (e: Exception) {
            println("DEBUG: 🎯 ❌ Ultra-fresh location failed: ${e.message}")
            LocationResult.Error(e.message ?: "Failed to get fresh location")
        }
    }
}
