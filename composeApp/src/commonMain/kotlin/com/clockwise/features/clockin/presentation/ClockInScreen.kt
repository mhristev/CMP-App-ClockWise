package com.clockwise.features.clockin.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.clockwise.features.location.domain.model.ClockInEligibility

@Composable
fun ClockInScreen(
    viewModel: ClockInViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Clock In/Out",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Status Card
        StatusCard(
            state = state,
            onRefresh = { viewModel.onAction(ClockInAction.RefreshStatus) }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Location Status
        LocationStatusCard(
            state = state,
            onCheckLocation = { viewModel.onAction(ClockInAction.CheckEligibility) }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Clock In/Out Button
        ClockInOutButton(
            state = state,
            onClockIn = { viewModel.onAction(ClockInAction.ClockIn) },
            onClockOut = { viewModel.onAction(ClockInAction.ClockOut) }
        )
        
        // Error handling
        state.errorMessage?.let { error ->
            LaunchedEffect(error) {
                // Show error snackbar or dialog
            }
            
            AlertDialog(
                onDismissRequest = { viewModel.onAction(ClockInAction.DismissError) },
                title = { Text("Error") },
                text = { Text(error) },
                confirmButton = {
                    TextButton(onClick = { viewModel.onAction(ClockInAction.DismissError) }) {
                        Text("OK")
                    }
                }
            )
        }
        
        // Permission dialog
        if (state.showPermissionDialog) {
            LocationPermissionDialog(
                onRequestPermission = { viewModel.onAction(ClockInAction.RequestLocationPermission) },
                onDismiss = { viewModel.onAction(ClockInAction.DismissPermissionDialog) }
            )
        }
    }
}

@Composable
private fun StatusCard(
    state: ClockInState,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (state.isClockedIn) Color.Green else Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (state.isClockedIn) "Clocked In" else "Not Clocked In",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            if (state.lastClockInResponse != null) {
                Text(
                    text = "Last clock-in: ${formatTimestamp(state.lastClockInResponse.clockInTime)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(onClick = onRefresh) {
                Text("Refresh Status")
            }
        }
    }
}

@Composable
private fun LocationStatusCard(
    state: ClockInState,
    onCheckLocation: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = when (state.clockInEligibility) {
                        is ClockInEligibility.Eligible -> Color.Green
                        is ClockInEligibility.TooFarFromWorkplace -> Color.Red
                        else -> Color.Gray
                    }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Location Status",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (state.isLocationLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                val statusText = when (state.clockInEligibility) {
                    is ClockInEligibility.Eligible -> "✓ Within workplace area"
                    is ClockInEligibility.TooFarFromWorkplace -> "✗ Too far from workplace"
                    is ClockInEligibility.LocationUnavailable -> "⚠ Location unavailable"
                    is ClockInEligibility.NoBusinessUnitAssigned -> "⚠ No workplace assigned"
                    null -> "Tap to check location"
                }
                
                Text(
                    text = statusText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                
                state.userDistanceFromWorkplace?.let { distance ->
                    Text(
                        text = distance,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(
                onClick = onCheckLocation,
                enabled = !state.isLocationLoading
            ) {
                Text("Check Location")
            }
        }
    }
}

@Composable
private fun ClockInOutButton(
    state: ClockInState,
    onClockIn: () -> Unit,
    onClockOut: () -> Unit
) {
    val isEligible = state.clockInEligibility is ClockInEligibility.Eligible
    val buttonText = if (state.isClockedIn) "Clock Out" else "Clock In"
    val buttonColor = if (state.isClockedIn) {
        MaterialTheme.colors.secondary
    } else if (isEligible) {
        MaterialTheme.colors.primary
    } else {
        Color.Gray
    }
    
    Button(
        onClick = if (state.isClockedIn) onClockOut else onClockIn,
        enabled = !state.isLoading && (state.isClockedIn || isEligible),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
        shape = RoundedCornerShape(28.dp)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = buttonText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
    
    if (!isEligible && !state.isClockedIn) {
        Text(
            text = "You must be near your workplace to clock in",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun LocationPermissionDialog(
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.Orange
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Location Permission Required")
            }
        },
        text = {
            Text("To clock in, we need access to your location to verify you're at your workplace.")
        },
        confirmButton = {
            TextButton(onClick = onRequestPermission) {
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatTimestamp(timestamp: Long): String {
    // Simple timestamp formatting - in a real app you'd use proper date formatting
    return java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date(timestamp))
}
