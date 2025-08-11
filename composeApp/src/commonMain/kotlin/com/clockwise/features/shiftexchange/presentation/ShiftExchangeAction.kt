package com.clockwise.features.shiftexchange.presentation

import com.clockwise.features.shift.domain.model.Shift
import com.clockwise.features.shiftexchange.domain.model.ExchangeShift
import com.clockwise.features.shiftexchange.domain.model.RequestType

sealed interface ShiftExchangeAction {
    // Tab navigation
    data class SelectTab(val tab: ShiftExchangeTab) : ShiftExchangeAction
    
    // Load data
    data object LoadAvailableShifts : ShiftExchangeAction
    data object LoadMyPostedShifts : ShiftExchangeAction
    data object LoadUserShifts : ShiftExchangeAction
    data class LoadRequestsForShift(val exchangeShiftId: String) : ShiftExchangeAction
    
    // Post shift to marketplace
    data object ShowPostShiftDialog : ShiftExchangeAction
    data object HidePostShiftDialog : ShiftExchangeAction
    data class SelectShiftToPost(val shift: Shift) : ShiftExchangeAction
    data class PostShiftToMarketplace(val shift: Shift) : ShiftExchangeAction
    
    // Submit shift request for posted exchange shifts
    data class ShowRequestDialog(val exchangeShift: ExchangeShift) : ShiftExchangeAction
    data object HideRequestDialog : ShiftExchangeAction
    data class SubmitShiftRequest(
        val exchangeShiftId: String, 
        val requestType: RequestType,
        val swapShiftId: String? = null,
        val swapShiftPosition: String? = null,
        val swapShiftStartTime: String? = null,
        val swapShiftEndTime: String? = null,
        val requesterUserFirstName: String? = null,
        val requesterUserLastName: String? = null
    ) : ShiftExchangeAction
    
    // Manage requests for posted shifts
    data class ShowRequestsDialog(val exchangeShift: ExchangeShift) : ShiftExchangeAction
    data object HideRequestsDialog : ShiftExchangeAction
    data class AcceptRequest(
        val exchangeShiftId: String,
        val requestId: String
    ) : ShiftExchangeAction
    data class CancelExchangeShift(val exchangeShiftId: String) : ShiftExchangeAction
    
    // Error handling
    data object ClearError : ShiftExchangeAction
}