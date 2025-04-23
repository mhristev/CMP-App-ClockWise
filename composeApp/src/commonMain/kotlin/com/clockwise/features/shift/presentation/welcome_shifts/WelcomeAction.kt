package com.clockwise.features.shift.presentation.welcome_shifts

sealed interface WelcomeAction {
    object LoadUpcomingShifts : WelcomeAction
    data class ClockIn(val shiftId: String) : WelcomeAction
    data class ClockOut(val shiftId: String) : WelcomeAction
} 