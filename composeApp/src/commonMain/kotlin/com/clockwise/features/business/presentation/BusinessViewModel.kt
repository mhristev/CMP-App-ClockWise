package com.clockwise.features.business.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.features.auth.UserService
import com.clockwise.features.business.data.repository.UserRepository
import com.clockwise.core.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BusinessViewModel(
    private val userRepository: UserRepository,
    private val userService: UserService
) : ViewModel() {
    
    private val _state = MutableStateFlow(BusinessState())
    val state: StateFlow<BusinessState> = _state.asStateFlow()
    
    fun onAction(action: BusinessAction) {
        when (action) {
            is BusinessAction.LoadBusinessData -> loadBusinessData()
            is BusinessAction.SwitchView -> switchView(action.view)
            is BusinessAction.BusinessDataLoaded -> updateBusinessData(action.name, action.id)
            is BusinessAction.EmployeesLoaded -> updateEmployees(action.employees)
            is BusinessAction.Error -> handleError(action.message)
        }
    }
    
    private fun loadBusinessData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val currentUser = userService.currentUser.value
            if (currentUser != null) {
                val businessUnitId = currentUser.businessUnitId
                val businessUnitName = currentUser.businessUnitName
                
                if (businessUnitId != null && businessUnitName != null) {
                    _state.update { it.copy(
                        businessUnitId = businessUnitId,
                        businessUnitName = businessUnitName,
                        isLoading = false
                    ) }
                    
                    // TODO: Load employees from repository when available
                    // For now, we'll use mock data
                    val mockEmployees = listOf(
                        Employee("1", "John Doe", "john@example.com", "EMPLOYEE"),
                        Employee("2", "Jane Smith", "jane@example.com", "EMPLOYEE"),
                        Employee("3", "Bob Johnson", "bob@example.com", "MANAGER")
                    )
                    
                    _state.update { it.copy(employees = mockEmployees) }
                } else {
                    _state.update { it.copy(
                        isLoading = false,
                        error = "No business unit found for this user"
                    ) }
                }
            } else {
                _state.update { it.copy(
                    isLoading = false,
                    error = "User not logged in"
                ) }
            }
        }
    }
    
    private fun switchView(view: BusinessView) {
        _state.update { it.copy(currentView = view) }
    }
    
    private fun updateBusinessData(name: String, id: String) {
        _state.update { it.copy(
            businessUnitName = name,
            businessUnitId = id,
            isLoading = false
        ) }
    }
    
    private fun updateEmployees(employees: List<Employee>) {
        _state.update { it.copy(
            employees = employees,
            isLoading = false
        ) }
    }
    
    private fun handleError(message: String) {
        _state.update { it.copy(
            error = message,
            isLoading = false
        ) }
    }
}