package com.clockwise.features.clockin.presentation

import androidx.compose.runtime.*
import platform.CoreLocation.*
import kotlinx.cinterop.*

@Composable
actual fun LocationPermissionHandler(
    onPermissionResult: (Boolean) -> Unit,
    content: @Composable (requestPermission: () -> Unit) -> Unit
) {
    var permissionGranted by remember { 
        mutableStateOf(
            CLLocationManager.authorizationStatus() == kCLAuthorizationStatusAuthorizedWhenInUse ||
            CLLocationManager.authorizationStatus() == kCLAuthorizationStatusAuthorizedAlways
        )
    }
    
    // Log current permission status for debugging
    LaunchedEffect(Unit) {
        val status = CLLocationManager.authorizationStatus()
        println("iOS LocationPermissionHandler - Initial status: $status")
        println("iOS LocationPermissionHandler - Permission granted: $permissionGranted")
        onPermissionResult(permissionGranted)
    }
    
    content { 
        // For iOS, we'll show a dialog or redirect to settings
        // Since iOS permission requests need to be handled differently
        val currentStatus = CLLocationManager.authorizationStatus()
        println("iOS LocationPermissionHandler - Request permission clicked, status: $currentStatus")
        
        when (currentStatus) {
            kCLAuthorizationStatusNotDetermined -> {
                println("iOS LocationPermissionHandler - First time permission request")
                // First time - we can request permission
                val locationManager = CLLocationManager()
                locationManager.requestWhenInUseAuthorization()
                
                // For development, simulate permission granted
                // In production, you'd need to monitor the permission status change
                permissionGranted = true
                onPermissionResult(true)
            }
            kCLAuthorizationStatusDenied -> {
                println("iOS LocationPermissionHandler - Permission previously denied, need Settings")
                // Permission already denied - user needs to go to settings
                onPermissionResult(false)
            }
            kCLAuthorizationStatusRestricted -> {
                println("iOS LocationPermissionHandler - Permission restricted by system")
                onPermissionResult(false)
            }
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> {
                println("iOS LocationPermissionHandler - Permission already granted")
                permissionGranted = true
                onPermissionResult(true)
            }
            else -> {
                println("iOS LocationPermissionHandler - Unknown status: $currentStatus")
                onPermissionResult(false)
            }
        }
    }
}
