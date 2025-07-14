package com.clockwise.features.clockin.presentation

import android.Manifest
import androidx.compose.runtime.*
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun LocationPermissionHandler(
    onPermissionResult: (Boolean) -> Unit,
    content: @Composable (requestPermission: () -> Unit) -> Unit
) {
    // Multiple location permissions check
    val locationPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    // Track permission result
    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        onPermissionResult(locationPermissions.allPermissionsGranted)
    }
    
    content { locationPermissions.launchMultiplePermissionRequest() }
}
