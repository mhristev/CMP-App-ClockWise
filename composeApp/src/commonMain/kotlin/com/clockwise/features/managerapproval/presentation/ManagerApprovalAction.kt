package com.clockwise.features.managerapproval.presentation

sealed interface ManagerApprovalAction {
    data object LoadPendingExchanges : ManagerApprovalAction
    data object RefreshExchanges : ManagerApprovalAction
    data class ApproveExchange(val requestId: String) : ManagerApprovalAction
    data class RejectExchange(val requestId: String) : ManagerApprovalAction
    data class ShowConfirmationDialog(val requestId: String, val isApproval: Boolean) : ManagerApprovalAction
    data object DismissConfirmationDialog : ManagerApprovalAction
    data class SearchExchanges(val query: String) : ManagerApprovalAction
}