package com.clockwise.features.business.presentation.add_employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.core.UserService
import com.clockwise.features.business.data.repository.UserRepository
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val userRepository: UserRepository,
    private val userService: UserService
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.UpdateSearchQuery -> {
                handleSearch(action.query)
            }
            is SearchAction.Search -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    
                    searchUsers(action.query)
                }
            }
            is SearchAction.SearchSuccess -> {
                _state.update {
                    it.copy(
                        searchResults = action.users,
                        isLoading = false,
                        error = null
                    )
                }
            }
            is SearchAction.SearchError -> {
                _state.update {
                    it.copy(
                        error = action.error,
                        isLoading = false
                    )
                }
            }
            is SearchAction.AddUserToBusinessUnit -> {
                addUserToBusinessUnit(action.user)
            }
            is SearchAction.AddUserSuccess -> {
                _state.update {
                    it.copy(
                        successMessage = action.message,
                        isLoading = false,
                        error = null
                    )
                }
            }
            is SearchAction.AddUserError -> {
                _state.update {
                    it.copy(
                        error = action.error,
                        isLoading = false
                    )
                }
            }
            is SearchAction.ClearMessages -> {
                _state.update {
                    it.copy(
                        error = null,
                        successMessage = null
                    )
                }
            }
        }
    }

    private fun handleSearch(query: String) {
        // Cancel any existing search job
        viewModelScope.launch {
            if (query.isBlank()) {
                _state.update { it.copy(searchResults = emptyList()) }
                return@launch
            }

            delay(500) // Wait for 500ms after the last keystroke
            searchUsers(query)
        }
    }

    private suspend fun searchUsers(query: String) {
        _state.update { it.copy(isLoading = true) }

        try {
            userRepository.searchUsers(query)
                .collect { users ->
                    users.onSuccess {
                        _state.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                searchResults = it,
                                error = null
                            )
                        }
                    }
                    users.onError { error ->
                        _state.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                error = "An error occurred"
                            )
                        }
                    }
                }
        } catch (e: Exception) {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    error = e.message ?: "An error occurred"
                )
            }
        }
    }

    private fun addUserToBusinessUnit(user: User) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val businessUnitId = userService.getCurrentUserBusinessUnitId()
            if (businessUnitId != null) {
                try {
                    userRepository.addUserToBusinessUnit(user.id, businessUnitId)
                        .collect { result ->
                            result.onSuccess {
                                _state.update { currentState ->
                                    currentState.copy(
                                        isLoading = false,
                                        successMessage = "User ${user.username} added to your business unit successfully",
                                        error = null,
                                        searchResults = currentState.searchResults.filter { it.id != user.id }
                                    )
                                }
                            }
                            result.onError { error ->
                                _state.update { currentState ->
                                    currentState.copy(
                                        isLoading = false,
                                        error = "Failed to add user"
                                    )
                                }
                            }
                        }
                } catch (e: Exception) {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = e.message ?: "An error occurred while adding user"
                        )
                    }
                }
            } else {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "You are not associated with any business unit"
                    )
                }
            }
        }
    }
}