package com.clockwise.features.shift.presentation.welcome_shifts

import com.clockwise.features.shift.domain.model.Shift

data class WelcomeState(
    val todayShift: Shift? = null,
    val upcomingShifts: List<Shift> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
) 