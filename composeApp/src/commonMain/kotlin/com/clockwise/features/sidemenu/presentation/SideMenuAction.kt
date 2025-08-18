package com.clockwise.features.sidemenu.presentation

sealed interface SideMenuAction {
    data object ToggleMenu : SideMenuAction
    data object CloseMenu : SideMenuAction
    data object CallBusinessUnit : SideMenuAction
    data object EmailBusinessUnit : SideMenuAction
    data object GetDirections : SideMenuAction
    data object NavigateToSchedule : SideMenuAction
    data object NavigateToEmployeeList : SideMenuAction
    data object NavigateToSettings : SideMenuAction
    data object RefreshBusinessUnit : SideMenuAction
    
    // New navigation actions for drawer menu
    data object NavigateToHome : SideMenuAction
    data object NavigateToProfile : SideMenuAction
    data object NavigateToCalendar : SideMenuAction
    data object NavigateToClockIn : SideMenuAction
    data object NavigateToBusinessUnit : SideMenuAction
    data object NavigateToManagerApproval : SideMenuAction
    data object Logout : SideMenuAction
}