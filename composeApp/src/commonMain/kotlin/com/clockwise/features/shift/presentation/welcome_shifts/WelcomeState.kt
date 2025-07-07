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
    val error: String? = null
) 