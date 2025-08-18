package com.clockwise.features.managerapproval.presentation

import com.clockwise.features.managerapproval.domain.model.PendingExchangeShift

data class ManagerApprovalState(
    val pendingExchanges: List<PendingExchangeShift> = emptyList(),
    val filteredExchanges: List<PendingExchangeShift> = emptyList(),
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val showConfirmationDialog: Boolean = false,
    val selectedRequestId: String? = null,
    val isApprovalAction: Boolean = true
)