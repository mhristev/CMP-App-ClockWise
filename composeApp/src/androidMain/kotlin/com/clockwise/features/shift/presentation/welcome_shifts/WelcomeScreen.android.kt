package com.clockwise.features.shift.presentation.welcome_shifts

import androidx.compose.runtime.*
import com.clockwise.features.clockin.presentation.LocationPermissionHandler

@Composable
actual fun WelcomeScreenWithPermissions(
    state: WelcomeState,
    onAction: (WelcomeAction) -> Unit
) {
    var permissionRequested by remember { mutableStateOf(false) }
    
    LocationPermissionHandler(
        onPermissionResult = { granted ->
            if (permissionRequested) {
                if (granted) {
                    // Permission granted, proceed with location check
                    val shiftId = state.pendingClockInShiftId
                    if (shiftId != null) {
                        // Dismiss dialog and proceed with clock in
                        onAction(WelcomeAction.DismissLocationPermissionDialog)
                        onAction(WelcomeAction.ClockIn(shiftId))
                    }
                } else {
                    // Permission denied, dismiss dialog
                    onAction(WelcomeAction.DismissLocationPermissionDialog)
                }
                permissionRequested = false
            }
        }
    ) { requestPermission ->
        WelcomeScreen(
            state = state,
            onAction = { action ->
                when (action) {
                    is WelcomeAction.RequestLocationPermission -> {
                        // Request permission through Android permission handler
                        permissionRequested = true
                        requestPermission()
                    }
                    else -> {
                        onAction(action)
                    }
                }
            }
        )
    }
}
