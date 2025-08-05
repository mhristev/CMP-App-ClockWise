package com.clockwise.features.shift.presentation.welcome_shifts

import com.clockwise.features.consumption.domain.model.ConsumptionItem

sealed class WelcomeAction {
    data object LoadUpcomingShifts : WelcomeAction()
    data object RefreshShifts : WelcomeAction()
    data class ClockIn(val shiftId: String) : WelcomeAction()
    data class ClockOut(val shiftId: String) : WelcomeAction()
    data class SaveNote(val workSessionId: String, val note: String) : WelcomeAction()
    data class UpdateSessionNote(val workSessionId: String, val note: String) : WelcomeAction()
    data class ShowClockOutModal(val shiftId: String, val workSessionId: String?) : WelcomeAction()
    data object HideClockOutModal : WelcomeAction()
    data class ClockOutWithNote(val shiftId: String, val workSessionId: String?, val note: String) : WelcomeAction()
    data class UpdateClockOutNote(val note: String) : WelcomeAction()
    
    // Location-related actions
    data object RequestLocationPermission : WelcomeAction()
    data object DismissLocationPermissionDialog : WelcomeAction()
    data object DismissLocationRequiredDialog : WelcomeAction()
    data object DismissLocationOutOfRangeDialog : WelcomeAction()
    data object RetryLocationCheck : WelcomeAction()
    
    // Consumption items actions
    data class LoadConsumptionItems(val businessUnitId: String) : WelcomeAction()
    data class UpdateConsumptionItemQuantity(val item: ConsumptionItem, val quantity: Int) : WelcomeAction()
    data class SelectConsumptionType(val type: String?) : WelcomeAction()
    data class ClockOutWithNoteAndConsumption(
        val shiftId: String, 
        val workSessionId: String?, 
        val note: String
    ) : WelcomeAction()
} 