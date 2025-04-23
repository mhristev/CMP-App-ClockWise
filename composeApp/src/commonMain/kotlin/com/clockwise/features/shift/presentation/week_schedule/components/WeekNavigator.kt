package com.clockwise.features.shift.presentation.week_schedule.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clockwise.core.TimeProvider
import com.clockwise.features.shift.presentation.theme.ShiftColors
import com.clockwise.features.shift.presentation.week_schedule.WeeklyScheduleAction
import com.clockwise.features.shift.presentation.week_schedule.WeeklyScheduleState
import com.clockwise.core.util.formatDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.plus

@Composable
fun WeekNavigator(
    state: WeeklyScheduleState,
    onAction: (WeeklyScheduleAction) -> Unit
) {
    // Week navigation and days row
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ShiftColors.Background)
            .padding(8.dp)
    ) {
        // Navigation arrows and current week
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onAction(WeeklyScheduleAction.NavigateToPreviousWeek) }
            ) {
                Text("←", color = ShiftColors.Primary)
            }

            Text(
                text = formatDate(state.currentWeekStart),
                style = MaterialTheme.typography.subtitle1,
                color = ShiftColors.Primary
            )

            IconButton(
                onClick = { onAction(WeeklyScheduleAction.NavigateToNextWeek) }
            ) {
                Text("→", color = ShiftColors.Primary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Week days with dates
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            var currentDate = state.currentWeekStart
            val today = TimeProvider.getCurrentLocalDate()

            DayOfWeek.values().forEach { day ->
                val isToday = currentDate == today
                DayButtonWithDate(
                    day = day,
                    date = currentDate,
                    isSelected = state.selectedDay == day,
                    isToday = isToday,
                    onClick = { onAction(WeeklyScheduleAction.SelectDay(day)) }
                )
                currentDate = currentDate.plus(1, DateTimeUnit.DAY)
            }
        }
    }
}