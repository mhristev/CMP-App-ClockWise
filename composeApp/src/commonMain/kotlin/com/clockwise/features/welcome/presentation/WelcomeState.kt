package com.clockwise.features.welcome.presentation

import com.clockwise.features.welcome.domain.model.Shift

data class WelcomeState(
    val todayShift: Shift? = null,
    val upcomingShifts: List<Shift> = emptyList(),
    val isLoading: Boolean = true
) 