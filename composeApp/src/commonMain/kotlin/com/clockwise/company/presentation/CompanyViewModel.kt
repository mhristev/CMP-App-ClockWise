package com.clockwise.company.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.company.data.network.RemoteCompanyDataSource
import com.clockwise.company.domain.Company
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import com.clockwise.company.data.network.to
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class CompanyViewModel(
    private val dataSource: RemoteCompanyDataSource
): ViewModel() {
    private val _uiState = MutableStateFlow(CompanyState())
    val state: StateFlow<CompanyState> = _uiState
        .onStart {
            loadCompanies()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _uiState.value
        )


    private fun loadCompanies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = dataSource.getCompanies()
                .collect { companies ->
                    companies.onSuccess {
                        _uiState.value = _uiState.value.copy(companies = it.map { it.to() }, isLoading = false)
                    }
                    companies.onError { error ->
                        _uiState.value =
                            _uiState.value.copy(error = error.toString(), isLoading = false)
                    }

                }
        }
    }

    fun onCompanyClick(companyId: String) {
        // Handle navigation to business units screen
    }
}

data class CompanyState(
    val companies: List<Company> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)