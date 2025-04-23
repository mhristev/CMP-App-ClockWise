package com.clockwise.features.shift.presentation.week_schedule

import kotlinx.datetime.DayOfWeek

sealed class WeeklyScheduleAction {
    data object LoadWeeklySchedule : WeeklyScheduleAction()
    data class SelectDay(val day: DayOfWeek) : WeeklyScheduleAction()
    data object NavigateToNextWeek : WeeklyScheduleAction()
    data object NavigateToPreviousWeek : WeeklyScheduleAction()
    data object NavigateToCurrentWeek : WeeklyScheduleAction()
} 