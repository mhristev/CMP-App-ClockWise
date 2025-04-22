package com.clockwise.features.availability.calendar.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CalendarScreenRoot(
    viewModel: CalendarViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CalendarScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
} 