package com.clockwise.features.sidemenu.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.core.model.User
import com.clockwise.features.auth.UserService
import com.clockwise.features.organization.domain.repository.OrganizationRepository
import com.clockwise.features.sidemenu.platform.PlatformActions
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SideMenuViewModel(
    private val userService: UserService,
    private val organizationRepository: OrganizationRepository,
    private val platformActions: PlatformActions
) : ViewModel() {

    private val _state = MutableStateFlow(SideMenuState())
    val state: StateFlow<SideMenuState> = _state.asStateFlow()

    init {
        loadBusinessUnitData()
        // Also observe user changes to reload data when user info changes
        viewModelScope.launch {
            userService.currentUser.collect { user ->
                if (user != null && user.businessUnitId != null) {
                    if (_state.value.businessUnit == null || _state.value.error != null) {
                        loadBusinessUnitData()
                    }
                }
            }
        }
    }

    fun ensureBusinessUnitLoaded() {
        if (_state.value.businessUnit == null && !_state.value.isLoading) {
            loadBusinessUnitData()
        }
    }
    
    fun onAction(action: SideMenuAction) {
        when (action) {
            is SideMenuAction.ToggleMenu -> toggleMenu()
            is SideMenuAction.CloseMenu -> closeMenu()
            is SideMenuAction.RefreshBusinessUnit -> loadBusinessUnitData()
            is SideMenuAction.CallBusinessUnit -> handleCallBusinessUnit()
            is SideMenuAction.EmailBusinessUnit -> handleEmailBusinessUnit()
            is SideMenuAction.GetDirections -> handleGetDirections()
            is SideMenuAction.NavigateToSchedule -> handleNavigateToSchedule()
            is SideMenuAction.NavigateToEmployeeList -> handleNavigateToEmployeeList()
            is SideMenuAction.NavigateToSettings -> handleNavigateToSettings()
            // New navigation actions for drawer menu
            is SideMenuAction.NavigateToHome -> handleNavigateToHome()
            is SideMenuAction.NavigateToProfile -> handleNavigateToProfile()
            is SideMenuAction.NavigateToCalendar -> handleNavigateToCalendar()
            is SideMenuAction.NavigateToClockIn -> handleNavigateToClockIn()
            is SideMenuAction.NavigateToBusinessUnit -> handleNavigateToBusinessUnit()
            is SideMenuAction.Logout -> handleLogout()
        }
    }

    private fun toggleMenu() {
        _state.value = _state.value.copy(
            isMenuOpen = !_state.value.isMenuOpen
        )
    }

    private fun closeMenu() {
        _state.value = _state.value.copy(
            isMenuOpen = false
        )
    }

    private fun loadBusinessUnitData() {
        viewModelScope.launch {
            try {
                val currentUser = userService.currentUser.first()
                val businessUnitId = currentUser?.businessUnitId
                
                if (businessUnitId != null) {
                    _state.value = _state.value.copy(isLoading = true, error = null)
                    
                    organizationRepository.getBusinessUnitDetails(businessUnitId)
                        .onSuccess { businessUnit ->
                            _state.value = _state.value.copy(
                                businessUnit = businessUnit,
                                isLoading = false,
                                error = null
                            )
                        }
                        .onError { error ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = "Failed to load business unit information"
                            )
                        }
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "No business unit assigned to user"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Unexpected error loading business unit"
                )
            }
        }
    }

    private fun handleCallBusinessUnit() {
        val businessUnit = _state.value.businessUnit
        businessUnit?.phoneNumber?.let { phoneNumber ->
            platformActions.makePhoneCall(phoneNumber)
        }
    }

    private fun handleEmailBusinessUnit() {
        val businessUnit = _state.value.businessUnit
        businessUnit?.email?.let { email ->
            platformActions.sendEmail(email)
        }
    }

    private fun handleGetDirections() {
        val businessUnit = _state.value.businessUnit
        businessUnit?.let { unit ->
            platformActions.openDirections(
                latitude = unit.latitude,
                longitude = unit.longitude,
                locationName = unit.name
            )
        }
    }

    private fun handleNavigateToSchedule() {
        // Navigation will be handled by the calling screen
        closeMenu()
    }

    private fun handleNavigateToEmployeeList() {
        // Navigation will be handled by the calling screen
        closeMenu()
    }

    private fun handleNavigateToSettings() {
        // Navigation will be handled by the calling screen
        closeMenu()
    }

    private fun handleNavigateToHome() {
        // Navigation will be handled by the calling screen
        closeMenu()
    }

    private fun handleNavigateToProfile() {
        // Navigation will be handled by the calling screen
        closeMenu()
    }

    private fun handleNavigateToCalendar() {
        // Navigation will be handled by the calling screen
        closeMenu()
    }

    private fun handleNavigateToClockIn() {
        // Navigation will be handled by the calling screen
        closeMenu()
    }

    private fun handleNavigateToBusinessUnit() {
        // Navigation will be handled by the calling screen
        closeMenu()
    }

    private fun handleLogout() {
        viewModelScope.launch {
            try {
                userService.clearAllUserData()
                closeMenu()
                // Navigation to auth screen will be handled by the calling screen
            } catch (e: Exception) {
                // Handle logout error if needed
                println("Error during logout: ${e.message}")
            }
        }
    }
}