package com.clockwise.features.clockin.presentation

/**
 * Actions that can be performed on the clock-in screen.
 */
sealed class ClockInAction {
    data object CheckEligibility : ClockInAction()
    data object ClockIn : ClockInAction()
    data object ClockOut : ClockInAction()
    data object RequestLocationPermission : ClockInAction()
    data object DismissError : ClockInAction()
    data object DismissPermissionDialog : ClockInAction()
    data object RefreshStatus : ClockInAction()
}
