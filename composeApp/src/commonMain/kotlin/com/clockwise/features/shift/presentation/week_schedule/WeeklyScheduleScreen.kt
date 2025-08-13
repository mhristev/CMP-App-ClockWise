@file:OptIn(ExperimentalMaterialApi::class)

package com.clockwise.features.shift.presentation.week_schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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

    // Pull to refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = {
            onAction(WeeklyScheduleAction.LoadWeeklySchedule)
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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

            when {
                state.isLoading && state.weeklySchedule.isEmpty() -> {
                    LoadingIndicator()
                }
                state.error != null -> {
                    // Error state
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.error,
                            style = MaterialTheme.typography.body1,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.error
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { onAction(WeeklyScheduleAction.LoadWeeklySchedule) }
                        ) {
                            Text("Retry")
                        }
                    }
                }
                else -> {
                    DayScheduleContent(state = state)
                }
            }
        }

        // Pull to refresh indicator
        PullRefreshIndicator(
            refreshing = state.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color.White,
            contentColor = MaterialTheme.colors.primary
        )
    }
}