package com.clockwise.features.clockin.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class LocationService(
    private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    actual suspend fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    actual suspend fun requestLocationPermission(): Boolean {
        // Check if we already have permission
        if (hasLocationPermission()) {
            return true
        }
        
        // For now, just return false if we don't have permission
        // In a real implementation, you would need to use ActivityResultLauncher
        // or a permission library like Accompanist Permissions
        return false
    }
    
    actual suspend fun getCurrentLocation(): LocationResult = suspendCancellableCoroutine { continuation ->
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        if (!hasPermission) {
            continuation.resume(LocationResult.PermissionDenied)
            return@suspendCancellableCoroutine
        }
        
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
            continuation.resume(LocationResult.LocationDisabled)
            return@suspendCancellableCoroutine
        }
        
        val cancellationTokenSource = CancellationTokenSource()
        
        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(
                        LocationResult.Success(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                } else {
                    continuation.resume(LocationResult.Error("Unable to get current location"))
                }
            }.addOnFailureListener { exception ->
                continuation.resume(LocationResult.Error(exception.message ?: "Unknown location error"))
            }
            
        } catch (e: SecurityException) {
            continuation.resume(LocationResult.PermissionDenied)
        } catch (e: Exception) {
            continuation.resume(LocationResult.Error(e.message ?: "Unknown error"))
        }
        
        continuation.invokeOnCancellation {
            cancellationTokenSource.cancel()
        }
    }
}
