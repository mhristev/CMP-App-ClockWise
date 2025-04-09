package com.clockwise.user.presentation.home.search

data class SearchState(
    val isLoading: Boolean = false,
    val searchResults: List<User> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

sealed class SearchAction {
    data class Search(val query: String) : SearchAction()
    data class SearchSuccess(val users: List<User>) : SearchAction()
    data class SearchError(val error: String) : SearchAction()
    data class AddUserToBusinessUnit(val user: User) : SearchAction()
    data class AddUserSuccess(val message: String) : SearchAction()
    data class AddUserError(val error: String) : SearchAction()
    object ClearMessages : SearchAction()
} 