package com.clockwise.features.shift.presentation.welcome_shifts

import com.clockwise.features.shift.domain.model.Shift

data class WelcomeState(
    val isLoading: Boolean = false,
    val todayShift: Shift? = null,
    val upcomingShifts: List<Shift> = emptyList(),
    val sessionNotes: Map<String, String> = emptyMap(),
    val savingNoteForSession: String? = null, // Track which session note is being saved
    val showClockOutModal: Boolean = false,
    val clockOutModalShiftId: String? = null,
    val clockOutModalWorkSessionId: String? = null,
    val clockOutNote: String = "",
    val error: String? = null,
    // Location checking states
    val isCheckingLocation: Boolean = false,
    val showLocationPermissionDialog: Boolean = false,
    val showLocationRequiredDialog: Boolean = false,
    val showLocationOutOfRangeDialog: Boolean = false,
    val distanceFromWorkplace: Double? = null,
    val pendingClockInShiftId: String? = null, // Track which shift is waiting for location check
    val businessUnitLocation: Pair<Double, Double>? = null, // Cache business unit coordinates
    val businessUnitAddress: String? = null, // Store business unit address for display
    val userLocation: Pair<Double, Double>? = null, // Store user's current location (latitude, longitude)
    val userAddress: String? = null // Store user's address if available
) 