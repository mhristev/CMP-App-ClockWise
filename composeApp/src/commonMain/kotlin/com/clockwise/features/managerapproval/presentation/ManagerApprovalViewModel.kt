package com.clockwise.features.managerapproval.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.features.auth.UserService
import com.clockwise.features.managerapproval.domain.usecase.ApproveExchangeUseCase
import com.clockwise.features.managerapproval.domain.usecase.GetPendingExchangesUseCase
import com.clockwise.features.managerapproval.domain.usecase.RecheckConflictsUseCase
import com.clockwise.features.managerapproval.domain.usecase.RejectExchangeUseCase
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ManagerApprovalViewModel(
    private val userService: UserService,
    private val getPendingExchangesUseCase: GetPendingExchangesUseCase,
    private val approveExchangeUseCase: ApproveExchangeUseCase,
    private val rejectExchangeUseCase: RejectExchangeUseCase,
    private val recheckConflictsUseCase: RecheckConflictsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ManagerApprovalState())
    val state: StateFlow<ManagerApprovalState> = _state.asStateFlow()

    init {
        loadPendingExchanges()
    }

    fun onAction(action: ManagerApprovalAction) {
        when (action) {
            is ManagerApprovalAction.LoadPendingExchanges -> loadPendingExchanges()
            is ManagerApprovalAction.RefreshExchanges -> refreshExchanges()
            is ManagerApprovalAction.ApproveExchange -> approveExchange(action.requestId)
            is ManagerApprovalAction.RejectExchange -> rejectExchange(action.requestId)
            is ManagerApprovalAction.RecheckConflicts -> recheckConflicts(action.requestId)
            is ManagerApprovalAction.ShowConfirmationDialog -> showConfirmationDialog(action.requestId, action.isApproval)
            is ManagerApprovalAction.DismissConfirmationDialog -> dismissConfirmationDialog()
            is ManagerApprovalAction.SearchExchanges -> searchExchanges(action.query)
        }
    }

    private fun loadPendingExchanges() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            getPendingExchangesUseCase.execute()
                .onSuccess { exchanges ->
                    _state.value = _state.value.copy(
                        pendingExchanges = exchanges,
                        filteredExchanges = filterExchanges(exchanges, _state.value.searchQuery),
                        isLoading = false,
                        error = null
                    )
                }
                .onError { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Failed to load pending exchanges"
                    )
                }
        }
    }

    private fun refreshExchanges() {
        loadPendingExchanges()
    }

    private fun approveExchange(requestId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isProcessing = true)
            
            approveExchangeUseCase.execute(requestId)
                .onSuccess {
                    _state.value = _state.value.copy(
                        isProcessing = false,
                        showConfirmationDialog = false,
                        selectedRequestId = null
                    )
                    // Refresh the list to remove the approved exchange
                    refreshExchanges()
                }
                .onError { error ->
                    _state.value = _state.value.copy(
                        isProcessing = false,
                        error = "Failed to approve exchange"
                    )
                }
        }
    }

    private fun rejectExchange(requestId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isProcessing = true)
            
            rejectExchangeUseCase.execute(requestId)
                .onSuccess {
                    _state.value = _state.value.copy(
                        isProcessing = false,
                        showConfirmationDialog = false,
                        selectedRequestId = null
                    )
                    // Refresh the list to remove the rejected exchange
                    refreshExchanges()
                }
                .onError { error ->
                    _state.value = _state.value.copy(
                        isProcessing = false,
                        error = "Failed to reject exchange"
                    )
                }
        }
    }

    private fun recheckConflicts(requestId: String) {
        viewModelScope.launch {
            // Find the existing exchange
            val existingExchange = _state.value.pendingExchanges.find { it.requestId == requestId }
            if (existingExchange == null) {
                _state.value = _state.value.copy(
                    error = "Exchange not found",
                    recheckingRequests = _state.value.recheckingRequests - requestId
                )
                return@launch
            }

            // Add request to rechecking set
            _state.value = _state.value.copy(
                recheckingRequests = _state.value.recheckingRequests + requestId,
                error = null
            )
            
            recheckConflictsUseCase.execute(requestId, existingExchange)
                .onSuccess { updatedExchange ->
                    // Update the exchange in the current lists
                    val updatedPendingExchanges = _state.value.pendingExchanges.map { exchange ->
                        if (exchange.requestId == requestId) updatedExchange else exchange
                    }
                    
                    _state.value = _state.value.copy(
                        pendingExchanges = updatedPendingExchanges,
                        filteredExchanges = filterExchanges(updatedPendingExchanges, _state.value.searchQuery),
                        recheckingRequests = _state.value.recheckingRequests - requestId
                    )
                }
                .onError { error ->
                    _state.value = _state.value.copy(
                        recheckingRequests = _state.value.recheckingRequests - requestId,
                        error = "Failed to recheck conflicts"
                    )
                }
        }
    }

    private fun showConfirmationDialog(requestId: String, isApproval: Boolean) {
        _state.value = _state.value.copy(
            showConfirmationDialog = true,
            selectedRequestId = requestId,
            isApprovalAction = isApproval
        )
    }

    private fun dismissConfirmationDialog() {
        _state.value = _state.value.copy(
            showConfirmationDialog = false,
            selectedRequestId = null
        )
    }

    private fun searchExchanges(query: String) {
        _state.value = _state.value.copy(
            searchQuery = query,
            filteredExchanges = filterExchanges(_state.value.pendingExchanges, query)
        )
    }

    private fun filterExchanges(exchanges: List<com.clockwise.features.managerapproval.domain.model.PendingExchangeShift>, query: String): List<com.clockwise.features.managerapproval.domain.model.PendingExchangeShift> {
        if (query.isBlank()) return exchanges
        
        val lowercaseQuery = query.lowercase()
        return exchanges.filter { exchange ->
            val posterName = "${exchange.posterUserFirstName ?: ""} ${exchange.posterUserLastName ?: ""}".trim()
            val requesterName = "${exchange.requesterUserFirstName ?: ""} ${exchange.requesterUserLastName ?: ""}".trim()
            val position = exchange.shiftPosition ?: ""
            
            posterName.lowercase().contains(lowercaseQuery) ||
            requesterName.lowercase().contains(lowercaseQuery) ||
            position.lowercase().contains(lowercaseQuery) ||
            exchange.requestType.name.lowercase().contains(lowercaseQuery)
        }
    }
}