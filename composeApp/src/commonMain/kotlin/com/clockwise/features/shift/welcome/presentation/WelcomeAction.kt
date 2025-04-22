package com.clockwise.features.shift.welcome.presentation

sealed interface WelcomeAction {
    object LoadUpcomingShifts : WelcomeAction
    data class ClockIn(val shiftId: String) : WelcomeAction
    data class ClockOut(val shiftId: String) : WelcomeAction
} 