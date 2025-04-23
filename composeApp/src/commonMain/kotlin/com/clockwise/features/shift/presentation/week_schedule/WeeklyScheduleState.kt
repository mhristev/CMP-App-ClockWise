package com.clockwise.features.shift.presentation.week_schedule

import com.clockwise.core.TimeProvider
import com.clockwise.features.shift.domain.model.Shift
import com.clockwise.features.shift.domain.utils.getWeekStartDate
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class WeeklyScheduleState(
    val weeklySchedule: Map<DayOfWeek, List<Shift>> = emptyMap(),
    val selectedDay: DayOfWeek? = TimeProvider.getCurrentLocalDate().dayOfWeek,
    val currentWeekStart: LocalDate = getWeekStartDate(TimeProvider.getCurrentLocalDate()),
    val isLoading: Boolean = true,
    val error: String? = null
) 