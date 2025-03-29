package com.clockwise.user.presentation.home.search

import androidx.lifecycle.ViewModel
import com.clockwise.user.domain.repository.SearchRepository
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var searchJob: Job? = null

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.Search -> {
                handleSearch(action.query)
            }

            is SearchAction.SearchSuccess -> {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        searchResults = action.users,
                        error = null
                    )
                }
            }

            is SearchAction.SearchError -> {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = action.error
                    )
                }
            }
        }
    }

    private fun handleSearch(query: String) {
        // Cancel any existing search job
        searchJob?.cancel()

        if (query.isBlank()) {
            _state.update { it.copy(searchResults = emptyList()) }
            return
        }

        // Create a new search job with delay
        searchJob = viewModelScope.launch {
            delay(500) // Wait for 500ms after the last keystroke
            searchUsers(query)
        }
    }

    private suspend fun searchUsers(query: String) {
        _state.update { it.copy(isLoading = true) }

        try {
            searchRepository.searchUsers(query)
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

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}