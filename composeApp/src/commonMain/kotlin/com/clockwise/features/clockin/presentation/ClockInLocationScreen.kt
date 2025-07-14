package com.clockwise.features.clockin.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clockwise.features.location.domain.model.BusinessUnitAddress
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClockInScreen(
    businessUnitAddress: BusinessUnitAddress,
    viewModel: ClockInViewModel = koinViewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
    
    LocationPermissionHandler(
        onPermissionResult = { granted ->
            if (granted) {
                viewModel.onLocationPermissionGranted()
            } else {
                viewModel.onLocationPermissionDenied()
            }
        }
    ) { requestPermission ->
        
        val uiState = viewModel.uiState
        
        // Set the business unit address and check location permission status when screen loads
        LaunchedEffect(businessUnitAddress) {
            try {
                viewModel.setBusinessUnitAddress(businessUnitAddress)
                viewModel.checkLocationPermissionStatus()
            } catch (e: Exception) {
                // Handle any initialization errors gracefully
                println("Error initializing ClockIn screen: ${e.message}")
            }
        }
        
        // Auto-check location when permission is granted
        LaunchedEffect(uiState.locationPermissionStatus) {
            if (uiState.locationPermissionStatus == LocationPermissionStatus.Granted && 
                uiState.eligibility == null && 
                !uiState.isCheckingLocation) {
                // Automatically check location when permission is granted
                viewModel.checkLocationEligibility()
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            // Permission Status Card (for debugging)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = when (uiState.locationPermissionStatus) {
                    LocationPermissionStatus.Granted -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                    LocationPermissionStatus.Denied -> Color(0xFFF44336).copy(alpha = 0.1f)
                    LocationPermissionStatus.RequiresRequest -> Color(0xFFFF9800).copy(alpha = 0.1f)
                    LocationPermissionStatus.Unknown -> Color.Gray.copy(alpha = 0.1f)
                }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = when (uiState.locationPermissionStatus) {
                            LocationPermissionStatus.Granted -> Icons.Default.LocationOn
                            LocationPermissionStatus.Denied -> Icons.Default.Warning
                            LocationPermissionStatus.RequiresRequest -> Icons.Default.Warning
                            LocationPermissionStatus.Unknown -> Icons.Default.Info
                        },
                        contentDescription = null,
                        tint = when (uiState.locationPermissionStatus) {
                            LocationPermissionStatus.Granted -> Color(0xFF4CAF50)
                            LocationPermissionStatus.Denied -> Color(0xFFF44336)
                            LocationPermissionStatus.RequiresRequest -> Color(0xFFFF9800)
                            LocationPermissionStatus.Unknown -> Color.Gray
                        },
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = when (uiState.locationPermissionStatus) {
                            LocationPermissionStatus.Granted -> "Location Access: Granted ✓"
                            LocationPermissionStatus.Denied -> "Location Access: Denied ✗"
                            LocationPermissionStatus.RequiresRequest -> "Location Access: Required ⚠️"
                            LocationPermissionStatus.Unknown -> "Location Access: Checking..."
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = when (uiState.locationPermissionStatus) {
                            LocationPermissionStatus.Granted -> Color(0xFF4CAF50)
                            LocationPermissionStatus.Denied -> Color(0xFFF44336)
                            LocationPermissionStatus.RequiresRequest -> Color(0xFFFF9800)
                            LocationPermissionStatus.Unknown -> Color.Gray
                        }
                    )
                    
                    // Show permission request button when needed
                    if (uiState.locationPermissionStatus == LocationPermissionStatus.RequiresRequest ||
                        uiState.locationPermissionStatus == LocationPermissionStatus.Denied) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { requestPermission() },
                            enabled = !uiState.isRequestingPermission,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFFFF9800)
                            )
                        ) {
                            if (uiState.isRequestingPermission) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = if (uiState.locationPermissionStatus == LocationPermissionStatus.Denied) 
                                        "Open Settings" else "Grant Permission",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
            
            // Business Unit Info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = businessUnitAddress.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = businessUnitAddress.address,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Debug Coordinates Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = Color(0xFF2196F3).copy(alpha = 0.1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📍 Location Debug Info",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    // Business Unit Coordinates
                    Text(
                        text = "🏢 Business Location:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Lat: ${businessUnitAddress.latitude}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Text(
                        text = "Lng: ${businessUnitAddress.longitude}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                    )
                    
                    // Current Location (if available from eligibility check)
                    Text(
                        text = "📱 Your Location:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    if (uiState.eligibility?.hasPermission == true && uiState.eligibility?.currentLatitude != null) {
                        Text(
                            text = "Lat: ${uiState.eligibility?.currentLatitude}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Text(
                            text = "Lng: ${uiState.eligibility?.currentLongitude}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Text(
                            text = "Distance: ${uiState.eligibility?.distance?.toInt() ?: "Unknown"}m",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if ((uiState.eligibility?.distance ?: Double.MAX_VALUE) <= businessUnitAddress.allowedRadius) 
                                Color(0xFF4CAF50) else Color(0xFFF44336),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Text(
                            text = "Allowed radius: ${businessUnitAddress.allowedRadius}m",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    } else {
                        Text(
                            text = if (uiState.isCheckingLocation) "Getting location..." else "Location not available - Tap 'Check Location'",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    
                    // Distance calculation status
                    if (uiState.eligibility?.distance != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "📏 Distance Calculation:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = if (uiState.eligibility?.isEligible == true) 
                                "✅ Within allowed radius" 
                            else 
                                "❌ Outside allowed radius",
                            fontSize = 12.sp,
                            color = if (uiState.eligibility?.isEligible == true) 
                                Color(0xFF4CAF50) else Color(0xFFF44336),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
            
            // Clock In Button
            Button(
                onClick = {
                    isLoading = true
                    viewModel.checkLocationEligibility()
                    isLoading = false
                },
                enabled = !isLoading && !uiState.isCheckingLocation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF2196F3)
                )
            ) {
                if (isLoading || uiState.isCheckingLocation) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = if (uiState.eligibility?.isEligible == true) "🕒 Clock In" else "📍 Check Location",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Success/Error Messages
            uiState.message?.let { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    elevation = 4.dp,
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = if (uiState.isError) Color(0xFFF44336).copy(alpha = 0.1f) else Color(0xFF4CAF50).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = if (uiState.isError) Color(0xFFF44336) else Color(0xFF4CAF50)
                    )
                }
            }
        }
    } // End of LocationPermissionHandler content
} // End of LocationPermissionHandler

// Create an expect/actual for permission handling
@Composable
expect fun LocationPermissionHandler(
    onPermissionResult: (Boolean) -> Unit,
    content: @Composable (requestPermission: () -> Unit) -> Unit
)