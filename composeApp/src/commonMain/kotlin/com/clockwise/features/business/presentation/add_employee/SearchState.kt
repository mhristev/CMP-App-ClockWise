package com.clockwise.features.business.presentation.add_employee

data class SearchState(
    val isLoading: Boolean = false,
    val searchResults: List<User> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)