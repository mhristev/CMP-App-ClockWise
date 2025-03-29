package com.clockwise.user.presentation.home.search

data class SearchState(
    val isLoading: Boolean = false,
    val searchResults: List<User> = emptyList(),
    val error: String? = null
)

sealed class SearchAction {
    data class Search(val query: String) : SearchAction()
    data class SearchSuccess(val users: List<User>) : SearchAction()
    data class SearchError(val error: String) : SearchAction()
} 