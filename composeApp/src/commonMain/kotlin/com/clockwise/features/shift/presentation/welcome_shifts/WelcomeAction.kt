package com.clockwise.features.shift.presentation.welcome_shifts

sealed class WelcomeAction {
    data object LoadUpcomingShifts : WelcomeAction()
    data class ClockIn(val shiftId: String) : WelcomeAction()
    data class ClockOut(val shiftId: String) : WelcomeAction()
    data class SaveNote(val workSessionId: String, val note: String) : WelcomeAction()
} 