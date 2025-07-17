package com.clockwise.features.shift.presentation.welcome_shifts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.clockwise.features.shift.presentation.welcome_shifts.components.UpcomingShiftCard
import com.clockwise.features.shift.presentation.welcome_shifts.components.ClockOutModal
import com.clockwise.features.welcome.presentation.components.LocationPermissionDialog
import com.clockwise.features.welcome.presentation.components.LocationRequiredDialog
import com.clockwise.features.welcome.presentation.components.LocationOutOfRangeDialog

@Composable
fun WelcomeScreenRoot(
    viewModel: WelcomeViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Platform-specific permission handling
    WelcomeScreenWithPermissions(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}

@Composable
expect fun WelcomeScreenWithPermissions(
    state: WelcomeState,
    onAction: (WelcomeAction) -> Unit
)

@Composable
fun WelcomeScreen(
    state: WelcomeState,
    onAction: (WelcomeAction) -> Unit
) {
    LaunchedEffect(Unit) {
        onAction(WelcomeAction.LoadUpcomingShifts)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome back!",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A2B8C)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Today's Shift Section
        Text(
            text = "Today's Shift",
            style = MaterialTheme.typography.h6,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.isLoading || state.isCheckingLocation) {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF4A2B8C)
                    )
                    if (state.isCheckingLocation) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Checking location...",
                            style = MaterialTheme.typography.body2,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
        } else {
            state.todayShift?.let { shift ->
                val sessionNote = shift.workSession?.id?.let { workSessionId ->
                    state.sessionNotes[workSessionId] ?: ""
                } ?: ""
                val isSavingNote = shift.workSession?.id == state.savingNoteForSession
                
                UpcomingShiftCard(
                    shift = shift,
                    canClockInOut = true,
                    sessionNote = sessionNote,
                    isSavingNote = isSavingNote,
                    onAction = onAction
                )
            } ?: Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No shift scheduled for today",
                    style = MaterialTheme.typography.body1,
                    color = Color(0xFF666666)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Upcoming Shifts Section
        Text(
            text = "Upcoming Shifts",
            style = MaterialTheme.typography.h6,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.isLoading || state.isCheckingLocation) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF4A2B8C)
                )
            }
        } else if (state.upcomingShifts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No upcoming shifts",
                    style = MaterialTheme.typography.body1,
                    color = Color(0xFF666666)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.upcomingShifts) { shift ->
                    val sessionNote = shift.workSession?.id?.let { workSessionId ->
                        state.sessionNotes[workSessionId] ?: ""
                    } ?: ""
                    val isSavingNote = shift.workSession?.id == state.savingNoteForSession
                    
                    UpcomingShiftCard(
                        shift = shift,
                        canClockInOut = false,
                        sessionNote = sessionNote,
                        isSavingNote = isSavingNote,
                        onAction = onAction
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
    
    // Clock Out Modal
    ClockOutModal(
        isVisible = state.showClockOutModal,
        note = state.clockOutNote,
        isSaving = state.isLoading,
        onNoteChange = { onAction(WelcomeAction.UpdateClockOutNote(it)) },
        onConfirmClockOut = {
            if (state.clockOutModalShiftId != null) {
                onAction(
                    WelcomeAction.ClockOutWithNote(
                        shiftId = state.clockOutModalShiftId,
                        workSessionId = state.clockOutModalWorkSessionId,
                        note = state.clockOutNote
                    )
                )
            }
        },
        onDismiss = { onAction(WelcomeAction.HideClockOutModal) }
    )
    
    // Location Permission Dialog
    LocationPermissionDialog(
        isVisible = state.showLocationPermissionDialog,
        title = "Location Permission Required",
        message = "ClockWise needs location access to verify you're at your workplace for clocking in. Please allow location access to continue.",
        onAllowClick = { onAction(WelcomeAction.RequestLocationPermission) },
        onDenyClick = { onAction(WelcomeAction.DismissLocationPermissionDialog) },
        onDismiss = { onAction(WelcomeAction.DismissLocationPermissionDialog) }
    )
    
    // Location Required Dialog (when permission is denied)
    LocationRequiredDialog(
        isVisible = state.showLocationRequiredDialog,
        onRetryClick = { onAction(WelcomeAction.RetryLocationCheck) },
        onDismiss = { onAction(WelcomeAction.DismissLocationRequiredDialog) }
    )
    
    // Location Out of Range Dialog
    LocationOutOfRangeDialog(
        isVisible = state.showLocationOutOfRangeDialog,
        distance = state.distanceFromWorkplace ?: 0.0,
        businessUnitAddress = state.businessUnitAddress,
        userLatitude = state.userLocation?.first,
        userLongitude = state.userLocation?.second,
        userAddress = state.userAddress,
        onDismiss = { onAction(WelcomeAction.DismissLocationOutOfRangeDialog) }
    )
}
