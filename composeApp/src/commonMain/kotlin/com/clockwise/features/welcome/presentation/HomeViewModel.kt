package com.clockwise.features.welcome.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.core.UserService
import com.clockwise.features.availability.calendar.presentation.CalendarAction
import com.clockwise.features.availability.calendar.presentation.CalendarState
import com.clockwise.features.availability.calendar.presentation.CalendarViewModel
import com.clockwise.features.business.presentation.BusinessAction
import com.clockwise.features.business.presentation.BusinessState
import com.clockwise.features.business.presentation.BusinessViewModel
import com.clockwise.features.business.presentation.add_employee.SearchAction
import com.clockwise.features.business.presentation.add_employee.SearchState
import com.clockwise.features.profile.presentation.ProfileAction
import com.clockwise.features.profile.presentation.ProfileViewModel
import com.clockwise.features.shift.schedule.presentation.WeeklyScheduleAction
import com.clockwise.features.shift.schedule.presentation.WeeklyScheduleState
import com.clockwise.features.shift.schedule.presentation.WeeklyScheduleViewModel
import com.clockwise.features.shift.schedule.presentation.getWeekStartDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeViewModel(
    private val userService: UserService,
    private val welcomeViewModel: WelcomeViewModel,
    private val weeklyScheduleViewModel: WeeklyScheduleViewModel,
    private val calendarViewModel: CalendarViewModel,
    private val businessViewModel: BusinessViewModel,
    private val profileViewModel: ProfileViewModel
) : ViewModel() {

    // Initial state with default values - explicitly specify HomeScreen type
    private val _currentScreen = MutableStateFlow<HomeScreen>(HomeScreen.Welcome)
    
    // Combine all the states from child ViewModels into one HomeState
    val state: StateFlow<HomeState> = combine(
        _currentScreen,
        welcomeViewModel.state,
        weeklyScheduleViewModel.state,
        calendarViewModel.state,
        businessViewModel.state
    ) { currentScreen, welcomeState, weeklyScheduleState, calendarState, businessState ->
        HomeState(
            currentScreen = currentScreen,
            welcomeState = welcomeState,
            weeklyScheduleState = weeklyScheduleState,
            calendarState = calendarState,
            searchState = SearchState(), // This should be handled by a SearchViewModel in a real implementation
            businessState = businessState
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeState(
            currentScreen = HomeScreen.Welcome,
            welcomeState = WelcomeState(),
            weeklyScheduleState = WeeklyScheduleState(
                currentWeekStart = getWeekStartDate(Clock.System.now().toLocalDateTime(TimeZone.UTC).date)
            ),
            calendarState = CalendarState(
                currentMonth = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
            ),
            searchState = SearchState(),
            businessState = BusinessState()
        )
    )

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.NavigateToScreen -> {
                _currentScreen.update { action.screen }
            }
            is HomeAction.WelcomeScreenAction -> {
                welcomeViewModel.onAction(action.action)
            }
            is HomeAction.WeeklyScheduleScreenAction -> {
                weeklyScheduleViewModel.onAction(action.action)
            }
            is HomeAction.CalendarScreenAction -> {
                calendarViewModel.onAction(action.action)
            }
            is HomeAction.BusinessScreenAction -> {
                businessViewModel.onAction(action.action)
            }
            is HomeAction.ProfileScreenAction -> {
                profileViewModel.onAction(action.action)
            }
            is HomeAction.SearchScreenAction -> {
                // In a real implementation, this would call a SearchViewModel
                // handleSearchAction(action.action)
            }
        }
    }
}

sealed interface HomeAction {
    data class NavigateToScreen(val screen: HomeScreen) : HomeAction
    data class WelcomeScreenAction(val action: WelcomeAction) : HomeAction
    data class WeeklyScheduleScreenAction(val action: WeeklyScheduleAction) : HomeAction
    data class CalendarScreenAction(val action: CalendarAction) : HomeAction
    data class SearchScreenAction(val action: SearchAction) : HomeAction
    data class BusinessScreenAction(val action: BusinessAction) : HomeAction
    data class ProfileScreenAction(val action: ProfileAction) : HomeAction
}

data class HomeState(
    val currentScreen: HomeScreen = HomeScreen.Welcome,
    val welcomeState: WelcomeState = WelcomeState(),
    val weeklyScheduleState: WeeklyScheduleState = WeeklyScheduleState(
        currentWeekStart = getWeekStartDate(Clock.System.now().toLocalDateTime(TimeZone.UTC).date)
    ),
    val calendarState: CalendarState = CalendarState(
        currentMonth = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
    ),
    val searchState: SearchState = SearchState(),
    val businessState: BusinessState = BusinessState()
) 