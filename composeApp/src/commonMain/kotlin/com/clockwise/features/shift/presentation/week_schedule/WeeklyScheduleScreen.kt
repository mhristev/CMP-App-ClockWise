package com.clockwise.features.shift.presentation.week_schedule

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.clockwise.core.TimeProvider
import com.clockwise.features.shift.presentation.week_schedule.components.DayScheduleContent
import com.clockwise.features.shift.presentation.week_schedule.components.LoadingIndicator
import com.clockwise.features.shift.presentation.week_schedule.components.ScheduleHeader
import com.clockwise.features.shift.presentation.week_schedule.components.WeekNavigator

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

@Composable
fun WeeklyScheduleScreen(
    state: WeeklyScheduleState,
    onAction: (WeeklyScheduleAction) -> Unit
) {
    LaunchedEffect(Unit) {
        onAction(WeeklyScheduleAction.LoadWeeklySchedule)
        if (state.selectedDay == null) {
            val today = TimeProvider.getCurrentLocalDate()
            onAction(WeeklyScheduleAction.SelectDay(today.dayOfWeek))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScheduleHeader(
            onTodayClick = { onAction(WeeklyScheduleAction.NavigateToCurrentWeek) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        WeekNavigator(
            state = state,
            onAction = onAction
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            LoadingIndicator()
        } else {
            DayScheduleContent(state = state)
        }
    }
}