package com.clockwise.features.clockin.presentation

import com.clockwise.features.clockin.domain.model.ClockInResponse
import com.clockwise.features.location.domain.model.ClockInEligibility

/**
 * UI state for the clock-in screen.
 */
data class ClockInState(
    val isLoading: Boolean = false,
    val isLocationLoading: Boolean = false,
    val clockInEligibility: ClockInEligibility? = null,
    val isClockedIn: Boolean = false,
    val lastClockInResponse: ClockInResponse? = null,
    val errorMessage: String? = null,
    val showPermissionDialog: Boolean = false,
    val userDistanceFromWorkplace: String? = null
)
