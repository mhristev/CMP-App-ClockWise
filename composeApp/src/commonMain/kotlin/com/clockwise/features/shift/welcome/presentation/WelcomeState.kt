package com.clockwise.features.shift.welcome.presentation

import com.clockwise.features.shift.core.domain.model.Shift

data class WelcomeState(
    val todayShift: Shift? = null,
    val upcomingShifts: List<Shift> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
) 