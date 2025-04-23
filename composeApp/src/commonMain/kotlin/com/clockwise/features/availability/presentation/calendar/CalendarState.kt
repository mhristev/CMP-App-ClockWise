package com.clockwise.features.availability.presentation.calendar

import kotlinx.datetime.LocalDate

data class CalendarState(
    val currentMonth: LocalDate,
    val selectedDate: LocalDate? = null,
    val monthlySchedule: Map<LocalDate, Pair<String, String>> = emptyMap(),
    val availabilityIdMap: Map<LocalDate, String> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAvailabilityDialog: Boolean = false,
    val showDeleteConfirmationDialog: Boolean = false
) 