package com.clockwise.features.shiftexchange.presentation

import com.clockwise.features.shift.domain.model.Shift
import com.clockwise.features.shiftexchange.domain.model.ExchangeShift
import com.clockwise.features.shiftexchange.domain.model.ShiftRequest

data class ShiftExchangeState(
    val selectedTab: ShiftExchangeTab = ShiftExchangeTab.AVAILABLE_SHIFTS,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    
    // Available shifts tab - posted shifts for exchange from marketplace
    val availableShifts: List<ExchangeShift> = emptyList(),
    val isLoadingAvailableShifts: Boolean = false,
    
    // My posted exchanges tab  
    val myPostedShifts: List<ExchangeShift> = emptyList(),
    val myShiftRequests: Map<String, List<ShiftRequest>> = emptyMap(), // exchangeShiftId -> requests
    val isLoadingMyPostedShifts: Boolean = false,
    
    // User's own shifts (for creating exchanges and swaps)
    val userShifts: List<Shift> = emptyList(),
    val isLoadingUserShifts: Boolean = false,
    
    // Cancel operation state
    val cancellingExchangeShiftIds: Set<String> = emptySet(),
    
    // UI state
    val showPostShiftDialog: Boolean = false,
    val selectedShiftToPost: Shift? = null,
    val showRequestDialog: Boolean = false,
    val selectedExchangeShift: ExchangeShift? = null,
    val showRequestsDialog: Boolean = false,
    val selectedExchangeShiftForRequests: ExchangeShift? = null,
    
    // Current user context
    val currentBusinessUnitId: String? = null,
    val currentUserId: String? = null
)

enum class ShiftExchangeTab {
    AVAILABLE_SHIFTS,
    MY_POSTED_EXCHANGES
}