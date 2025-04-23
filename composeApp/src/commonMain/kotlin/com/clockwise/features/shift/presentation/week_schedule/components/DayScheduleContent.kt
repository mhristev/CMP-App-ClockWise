package com.clockwise.features.shift.presentation.week_schedule.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clockwise.features.shift.presentation.theme.ShiftColors
import com.clockwise.features.shift.presentation.week_schedule.WeeklyScheduleState

@Composable
fun DayScheduleContent(state: WeeklyScheduleState) {
    state.selectedDay?.let { selectedDay ->
        Text(
            text = selectedDay.name,
            style = MaterialTheme.typography.h6,
            color = ShiftColors.TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val daySchedule = state.weeklySchedule[selectedDay] ?: emptyList()
        if (daySchedule.isEmpty()) {
            NoShiftsMessage()
        } else {
            ShiftList(shifts = daySchedule)
        }
    }
}