package com.clockwise.features.business.presentation

import kotlinx.serialization.Serializable

enum class BusinessView {
    OVERVIEW,
    EMPLOYEES
}

data class BusinessState(
    val businessUnitName: String = "",
    val businessUnitId: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentView: BusinessView = BusinessView.OVERVIEW,
    val employees: List<Employee> = emptyList()
)

sealed interface BusinessAction {
    object LoadBusinessData : BusinessAction
    data class SwitchView(val view: BusinessView) : BusinessAction
    data class BusinessDataLoaded(val name: String, val id: String) : BusinessAction
    data class EmployeesLoaded(val employees: List<Employee>) : BusinessAction
    data class Error(val message: String) : BusinessAction
}

@Serializable
data class Employee(
    val id: String,
    val username: String,
    val email: String,
    val role: String
) 