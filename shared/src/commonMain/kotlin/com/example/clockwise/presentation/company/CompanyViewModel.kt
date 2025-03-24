package com.example.clockwise.presentation.company

import com.example.clockwise.data.model.Company
import com.example.clockwise.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CompanyViewModel(
    private val repository: CompanyRepository
) {
    private val _uiState = MutableStateFlow(CompanyUiState())
    val uiState: StateFlow<CompanyUiState> = _uiState.asStateFlow()

    init {
        loadCompanies()
    }

    private suspend fun loadCompanies() {
        try {
            _uiState.update { it.copy(isLoading = true) }
            val companies = repository.getCompanies()
            _uiState.update { 
                it.copy(
                    companies = companies,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    error = e.message ?: "Unknown error occurred",
                    isLoading = false
                )
            }
        }
    }

    fun onCompanyClick(companyId: String) {
        // Handle navigation to business units screen
    }
}

data class CompanyUiState(
    val companies: List<Company> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 