package com.clockwise.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.features.auth.UserService
import com.clockwise.features.profile.domain.repository.UserProfileRepository
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val userService: UserService,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            try {
                println("SplashViewModel: Starting initialization...")
                _state.value = _state.value.copy(
                    isInitializing = true,
                    initializationMessage = "Starting up..."
                )
                
                // Add a small delay to ensure splash screen is visible
                delay(1000)
                
                // Check if we have existing auth data
                val hasStoredUser = userService.currentUser.value != null
                val hasValidToken = userService.getValidAuthToken() != null
                
                println("SplashViewModel: hasStoredUser=$hasStoredUser, hasValidToken=$hasValidToken")
                
                if (hasStoredUser && !hasValidToken) {
                    // We have user data but no valid token - attempt refresh
                    println("SplashViewModel: Attempting token refresh...")
                    _state.value = _state.value.copy(
                        isRefreshingToken = true,
                        initializationMessage = "Signing you in..."
                    )
                    
                    try {
                        userService.initializeAuthState()
                        
                        // If we now have a token, fetch fresh user profile
                        if (userService.getValidAuthToken() != null) {
                            userProfileRepository.getUserProfile()
                                .onSuccess { user ->
                                    userService.saveUser(user)
                                    _state.value = _state.value.copy(
                                        isAuthenticated = true,
                                        hasBusinessUnit = user.businessUnitId != null,
                                        initializationMessage = "Welcome back!"
                                    )
                                    println("SplashViewModel: Refresh successful, authenticated with business unit: ${user.businessUnitId != null}")
                                }
                                .onError { error ->
                                    // Token is valid but profile fetch failed
                                    _state.value = _state.value.copy(
                                        isAuthenticated = true,
                                        hasBusinessUnit = false,
                                        initializationMessage = "Profile sync incomplete"
                                    )
                                    println("SplashViewModel: Profile fetch failed: $error")
                                }
                        } else {
                            // Refresh failed - user needs to login
                            _state.value = _state.value.copy(
                                isAuthenticated = false,
                                hasBusinessUnit = false,
                                initializationMessage = "Please sign in"
                            )
                            println("SplashViewModel: Token refresh failed")
                        }
                    } catch (e: Exception) {
                        // Refresh failed - clear data and show login
                        userService.clearAllUserData()
                        _state.value = _state.value.copy(
                            isAuthenticated = false,
                            hasBusinessUnit = false,
                            initializationMessage = "Session expired"
                        )
                        println("SplashViewModel: Exception during refresh: ${e.message}")
                    }
                } else if (hasStoredUser && hasValidToken) {
                    // We have both user and valid token
                    _state.value = _state.value.copy(
                        isAuthenticated = true,
                        hasBusinessUnit = userService.currentUser.value?.businessUnitId != null,
                        initializationMessage = "Welcome back!"
                    )
                    println("SplashViewModel: User already authenticated")
                } else {
                    // No stored auth data - new user
                    _state.value = _state.value.copy(
                        isAuthenticated = false,
                        hasBusinessUnit = false,
                        initializationMessage = "Welcome to ClockWise"
                    )
                    println("SplashViewModel: New user, showing auth")
                }
                
                // Small delay to ensure smooth transition
                delay(500)
                
            } catch (e: Exception) {
                println("SplashViewModel: Outer exception: ${e.message}")
                _state.value = _state.value.copy(
                    isAuthenticated = false,
                    hasBusinessUnit = false,
                    initializationMessage = "Something went wrong"
                )
            } finally {
                println("SplashViewModel: Initialization complete")
                _state.value = _state.value.copy(
                    isInitializing = false,
                    isRefreshingToken = false,
                    initializationComplete = true
                )
            }
        }
    }
}

data class SplashState(
    val isInitializing: Boolean = false,
    val isRefreshingToken: Boolean = false,
    val isAuthenticated: Boolean = false,
    val hasBusinessUnit: Boolean = false,
    val initializationComplete: Boolean = false,
    val initializationMessage: String = "Loading..."
) 