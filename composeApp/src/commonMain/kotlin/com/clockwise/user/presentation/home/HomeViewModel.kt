package com.clockwise.user.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.user.presentation.home.calendar.CalendarAction
import com.clockwise.user.presentation.home.calendar.CalendarState
import com.clockwise.user.presentation.home.profile.ProfileAction
import com.clockwise.user.presentation.home.profile.ProfileState
import com.clockwise.user.presentation.home.schedule.WeeklyScheduleAction
import com.clockwise.user.presentation.home.schedule.WeeklyScheduleState
import com.clockwise.user.presentation.home.welcome.WelcomeAction
import com.clockwise.user.presentation.home.welcome.WelcomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.Navigate -> {
                _state.update { it.copy(currentScreen = action.screen) }
            }
            is HomeAction.WelcomeScreenAction -> handleWelcomeAction(action.action)
            is HomeAction.WeeklyScheduleScreenAction -> handleWeeklyScheduleAction(action.action)
            is HomeAction.CalendarScreenAction -> handleCalendarAction(action.action)
            is HomeAction.ProfileScreenAction -> handleProfileAction(action.action)
        }
    }

    private fun handleWelcomeAction(action: WelcomeAction) {
        when (action) {
            is WelcomeAction.LoadUpcomingShifts -> {
                viewModelScope.launch {
                    // TODO: Load upcoming shifts from repository
                    _state.update { 
                        it.copy(
                            welcomeState = it.welcomeState.copy(
                                upcomingShifts = emptyList(),
                                isLoading = false
                            )
                        )
                    }
                }
            }
        }
    }

    private fun handleWeeklyScheduleAction(action: WeeklyScheduleAction) {
        when (action) {
            is WeeklyScheduleAction.LoadWeeklySchedule -> {
                viewModelScope.launch {
                    // TODO: Load weekly schedule from repository
                    _state.update {
                        it.copy(
                            weeklyScheduleState = it.weeklyScheduleState.copy(
                                weeklySchedule = emptyMap(),
                                isLoading = false
                            )
                        )
                    }
                }
            }
            is WeeklyScheduleAction.SelectDay -> {
                _state.update {
                    it.copy(
                        weeklyScheduleState = it.weeklyScheduleState.copy(
                            selectedDay = action.day
                        )
                    )
                }
            }
        }
    }

    private fun handleCalendarAction(action: CalendarAction) {
        when (action) {
            is CalendarAction.LoadMonthlySchedule -> {
                viewModelScope.launch {
                    // TODO: Load monthly schedule from repository
                    _state.update {
                        it.copy(
                            calendarState = it.calendarState.copy(
                                monthlySchedule = emptyMap(),
                                isLoading = false
                            )
                        )
                    }
                }
            }
            is CalendarAction.SelectDate -> {
                _state.update {
                    it.copy(
                        calendarState = it.calendarState.copy(
                            selectedDate = action.date
                        )
                    )
                }
            }
            is CalendarAction.SetAvailability -> {
                viewModelScope.launch {
                    // TODO: Save availability to repository
                    _state.update {
                        it.copy(
                            calendarState = it.calendarState.copy(
                                isLoading = false
                            )
                        )
                    }
                }
            }
            is CalendarAction.NavigateToNextMonth -> {
                val currentDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
                val targetDate = _state.value.calendarState.currentMonth.plus(1, DateTimeUnit.MONTH)
                if (targetDate <= currentDate.plus(3, DateTimeUnit.MONTH)) {
                    _state.update { state ->
                        state.copy(
                            calendarState = state.calendarState.copy(
                                currentMonth = targetDate
                            )
                        )
                    }
                }
            }
            is CalendarAction.NavigateToPreviousMonth -> {
                val currentDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
                val targetDate = _state.value.calendarState.currentMonth.minus(1, DateTimeUnit.MONTH)
                if (targetDate >= currentDate.minus(3, DateTimeUnit.MONTH)) {
                    _state.update { state ->
                        state.copy(
                            calendarState = state.calendarState.copy(
                                currentMonth = targetDate
                            )
                        )
                    }
                }
            }
        }
    }

    private fun handleProfileAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.LoadUserProfile -> {
                viewModelScope.launch {
                    // TODO: Load user profile from repository
                    _state.update {
                        it.copy(
                            profileState = it.profileState.copy(
                                isLoading = false
                            )
                        )
                    }
                }
            }
            is ProfileAction.UpdateProfile -> {
                viewModelScope.launch {
                    // TODO: Update user profile in repository
                    _state.update {
                        it.copy(
                            profileState = it.profileState.copy(
                                isLoading = false
                            )
                        )
                    }
                }
            }
        }
    }
}

sealed interface HomeAction {
    data class Navigate(val screen: HomeScreen) : HomeAction
    data class WelcomeScreenAction(val action: WelcomeAction) : HomeAction
    data class WeeklyScheduleScreenAction(val action: WeeklyScheduleAction) : HomeAction
    data class CalendarScreenAction(val action: CalendarAction) : HomeAction
    data class ProfileScreenAction(val action: ProfileAction) : HomeAction
}

data class HomeState(
    val currentScreen: HomeScreen = HomeScreen.Welcome,
    val welcomeState: WelcomeState = WelcomeState(),
    val weeklyScheduleState: WeeklyScheduleState = WeeklyScheduleState(),
    val calendarState: CalendarState = CalendarState(
        currentMonth = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
    ),
    val profileState: ProfileState = ProfileState()
) 