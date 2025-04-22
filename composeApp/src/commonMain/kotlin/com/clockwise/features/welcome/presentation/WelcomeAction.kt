package com.clockwise.features.welcome.presentation

sealed interface WelcomeAction {
    object LoadUpcomingShifts : WelcomeAction
    data class ClockIn(val shiftId: Int) : WelcomeAction
    data class ClockOut(val shiftId: Int) : WelcomeAction
} 