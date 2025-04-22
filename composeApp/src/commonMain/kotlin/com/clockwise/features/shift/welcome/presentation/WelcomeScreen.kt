package com.clockwise.features.shift.welcome.presentation

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
import com.clockwise.features.shift.welcome.presentation.components.UpcomingShiftCard

@Composable
fun WelcomeScreenRoot(
    viewModel: WelcomeViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    WelcomeScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}

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

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF4A2B8C)
                )
            }
        } else {
            state.todayShift?.let { shift ->
                UpcomingShiftCard(
                    shift = shift,
                    onAction = onAction,
                    canClockInOut = true
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

        if (state.isLoading) {
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
                    UpcomingShiftCard(
                        shift = shift,
                        onAction = onAction,
                        canClockInOut = false
                    )
                }
            }
        }
    }
}
