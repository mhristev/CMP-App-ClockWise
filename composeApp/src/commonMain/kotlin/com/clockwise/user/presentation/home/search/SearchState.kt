package com.clockwise.user.presentation.home.search

data class SearchState(
    val isLoading: Boolean = false,
    val searchResults: List<User> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)