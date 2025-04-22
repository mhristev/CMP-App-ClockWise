package com.clockwise.features.shift.schedule.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WeeklyScheduleScreenRoot(
    viewModel: WeeklyScheduleViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    WeeklyScheduleScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
} 