package com.clockwise.features.business.presentation.add_employee

import com.clockwise.core.model.User

sealed interface SearchAction {
    data class UpdateSearchQuery(val query: String) : SearchAction
    data class Search(val query: String) : SearchAction
    data class SearchSuccess(val users: List<User>) : SearchAction
    data class SearchError(val error: String) : SearchAction
    data class AddUserToBusinessUnit(val user: User) : SearchAction
    data class AddUserSuccess(val message: String) : SearchAction
    data class AddUserError(val error: String) : SearchAction
    object ClearMessages : SearchAction
} 