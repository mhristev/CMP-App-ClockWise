@file:OptIn(ExperimentalMaterialApi::class)

package com.clockwise.features.shift.presentation.welcome_shifts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.clockwise.features.shift.presentation.welcome_shifts.components.UpcomingShiftCard
import com.clockwise.features.shift.presentation.welcome_shifts.components.ClockOutBottomSheet
import com.clockwise.features.shift.presentation.welcome_shifts.components.generateSummaryText
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

    // Pull to refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = {
            onAction(WelcomeAction.RefreshShifts)
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Today's Shift",
                    style = MaterialTheme.typography.h6,
                    color = Color(0xFF333333)
                )
            }

            item {
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
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Upcoming Shifts",
                    style = MaterialTheme.typography.h6,
                    color = Color(0xFF333333)
                )
            }

            if (state.isLoading || state.isCheckingLocation) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF4A2B8C)
                        )
                    }
                }
            } else if (state.upcomingShifts.isEmpty()) {
                item {
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
                }
            } else {
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
                }
            }
        }

        // Pull to refresh indicator
        PullRefreshIndicator(
            refreshing = state.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color.White,
            contentColor = Color(0xFF4A2B8C)
        )
    }
    
    // Clock Out Bottom Sheet
    ClockOutBottomSheet(
        isVisible = state.showClockOutModal,
        note = state.clockOutNote,
        isSaving = state.isLoading,
        onNoteChange = { onAction(WelcomeAction.UpdateClockOutNote(it)) },
        onConfirmClockOut = {
            if (state.clockOutModalShiftId != null) {
                // Generate summary text that combines note and consumption items
                val summaryText = generateSummaryText(
                    note = state.clockOutNote,
                    selectedConsumptionItems = state.selectedConsumptionItems
                )
                onAction(
                    WelcomeAction.ClockOutWithNoteAndConsumption(
                        shiftId = state.clockOutModalShiftId,
                        workSessionId = state.clockOutModalWorkSessionId,
                        note = summaryText // Send the formatted summary instead of raw note
                    )
                )
            }
        },
        onDismiss = { onAction(WelcomeAction.HideClockOutModal) },
        // Consumption items parameters
        consumptionItems = state.consumptionItems,
        selectedConsumptionItems = state.selectedConsumptionItems,
        selectedConsumptionType = state.selectedConsumptionType,
        isLoadingConsumptionItems = state.isLoadingConsumptionItems,
        onConsumptionItemQuantityChanged = { item, quantity ->
            onAction(WelcomeAction.UpdateConsumptionItemQuantity(item, quantity))
        },
        onConsumptionTypeSelected = { type ->
            onAction(WelcomeAction.SelectConsumptionType(type))
        }
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
