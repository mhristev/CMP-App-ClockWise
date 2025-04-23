package com.clockwise.features.availability.presentation.calendar

import kotlinx.datetime.LocalDate

sealed class CalendarAction {
    data object LoadMonthlySchedule : CalendarAction()
    data class SelectDate(val date: LocalDate) : CalendarAction()
    data class SetAvailability(
        val date: LocalDate,
        val startTime: String,
        val endTime: String
    ) : CalendarAction()
    data class EditAvailability(
        val date: LocalDate,
        val startTime: String,
        val endTime: String
    ) : CalendarAction()
    data class DeleteAvailability(val date: LocalDate) : CalendarAction()
    data object ShowAvailabilityDialog : CalendarAction()
    data object HideAvailabilityDialog : CalendarAction()
    data class ShowDeleteConfirmation(val date: LocalDate) : CalendarAction()
    data object HideDeleteConfirmation : CalendarAction()
    data object NavigateToNextMonth : CalendarAction()
    data object NavigateToPreviousMonth : CalendarAction()
    data object NavigateToCurrentMonth : CalendarAction()
} 