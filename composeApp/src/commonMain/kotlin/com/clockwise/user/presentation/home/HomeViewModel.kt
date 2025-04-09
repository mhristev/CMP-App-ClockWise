package com.clockwise.user.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.service.UserService
import com.clockwise.user.domain.UserRole
import com.clockwise.user.domain.AccessControl
import com.clockwise.user.presentation.home.calendar.CalendarAction
import com.clockwise.user.presentation.home.calendar.CalendarState
import com.clockwise.user.presentation.home.profile.ProfileAction
import com.clockwise.user.presentation.home.profile.ProfileState
import com.clockwise.user.presentation.home.schedule.WeeklyScheduleAction
import com.clockwise.user.presentation.home.schedule.WeeklyScheduleState
import com.clockwise.user.presentation.home.search.SearchAction
import com.clockwise.user.presentation.home.search.SearchState
import com.clockwise.user.presentation.home.search.SearchViewModel
import com.clockwise.user.presentation.home.welcome.ShiftStatus
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
import com.clockwise.navigation.NavigationRoutes
import com.clockwise.user.presentation.home.HomeScreen
import com.clockwise.user.presentation.home.business.BusinessAction
import com.clockwise.user.presentation.home.business.BusinessState
import com.clockwise.user.presentation.home.business.BusinessViewModel

class HomeViewModel(
    private val searchViewModel: SearchViewModel,
    private val userService: UserService,
    private val businessViewModel: BusinessViewModel
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    init {
        // Collect search state updates
        viewModelScope.launch {
            searchViewModel.state.collect { searchState ->
                _state.update { currentState ->
                    currentState.copy(searchState = searchState)
                }
            }
        }
        
        // Collect business state updates
        viewModelScope.launch {
            businessViewModel.state.collect { businessState ->
                _state.update { currentState ->
                    currentState.copy(businessState = businessState)
                }
            }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.Navigate -> {
                if (action.screen == HomeScreen.Search) {
                    if (!AccessControl.hasAccessToScreen("search", userService)) {
                        _state.update { it.copy(currentScreen = HomeScreen.Welcome) }
                        return
                    }
                }
                
                // Only allow managers and admins to access the Business screen
                if (action.screen == HomeScreen.Business) {
                    if (!AccessControl.hasAccessToScreen("business", userService)) {
                        _state.update { it.copy(currentScreen = HomeScreen.Welcome) }
                        return
                    }
                }
                
                _state.update { it.copy(currentScreen = action.screen) }
            }
            is HomeAction.WelcomeScreenAction -> handleWelcomeAction(action.action)
            is HomeAction.WeeklyScheduleScreenAction -> handleWeeklyScheduleAction(action.action)
            is HomeAction.CalendarScreenAction -> handleCalendarAction(action.action)
            is HomeAction.SearchScreenAction -> {
                // Only allow access if user has permission
                if (AccessControl.hasAccessToScreen("search", userService)) {
                    searchViewModel.onAction(action.action)
                }
            }
            is HomeAction.BusinessScreenAction -> {
                // Only allow access if user has permission
                if (AccessControl.hasAccessToScreen("business", userService)) {
                    businessViewModel.onAction(action.action)
                }
            }
        }
    }

    private fun handleWelcomeAction(action: WelcomeAction) {
        when (action) {
            is WelcomeAction.LoadUpcomingShifts -> {
                viewModelScope.launch {
                    val now = Clock.System.now()
                    val nowLocal = now.toLocalDateTime(TimeZone.UTC)
                    
                    // Today's shift
                    val todayShift = com.clockwise.user.presentation.home.welcome.Shift(
                        id = 1,
                        date = nowLocal,
                        startTime = nowLocal,
                        endTime = now.plus(8, DateTimeUnit.HOUR, TimeZone.UTC).toLocalDateTime(TimeZone.UTC),
                        location = "Main Office",
                        status = ShiftStatus.SCHEDULED
                    )
                    
                    // Upcoming shifts
                    val upcomingShifts = listOf(
                        com.clockwise.user.presentation.home.welcome.Shift(
                            id = 2,
                            date = now.plus(24, DateTimeUnit.HOUR, TimeZone.UTC).toLocalDateTime(TimeZone.UTC),
                            startTime = now.plus(24, DateTimeUnit.HOUR, TimeZone.UTC).toLocalDateTime(TimeZone.UTC),
                            endTime = now.plus(32, DateTimeUnit.HOUR, TimeZone.UTC).toLocalDateTime(TimeZone.UTC),
                            location = "Branch Office",
                            status = ShiftStatus.SCHEDULED
                        ),
                        com.clockwise.user.presentation.home.welcome.Shift(
                            id = 3,
                            date = now.plus(48, DateTimeUnit.HOUR, TimeZone.UTC).toLocalDateTime(TimeZone.UTC),
                            startTime = now.plus(48, DateTimeUnit.HOUR, TimeZone.UTC).toLocalDateTime(TimeZone.UTC),
                            endTime = now.plus(56, DateTimeUnit.HOUR, TimeZone.UTC).toLocalDateTime(TimeZone.UTC),
                            location = "Remote Office",
                            status = ShiftStatus.SCHEDULED
                        )
                    )
                    
                    _state.update { 
                        it.copy(
                            welcomeState = it.welcomeState.copy(
                                todayShift = todayShift,
                                upcomingShifts = upcomingShifts,
                                isLoading = false
                            )
                        )
                    }
                }
            }
            is WelcomeAction.ClockIn -> {
                viewModelScope.launch {
                    // Update the shift status to CLOCKED_IN and set clockInTime
                    _state.update { currentState ->
                        currentState.copy(
                            welcomeState = currentState.welcomeState.copy(
                                todayShift = currentState.welcomeState.todayShift?.let { shift ->
                                    if (shift.id == action.shiftId) {
                                        shift.copy(
                                            status = ShiftStatus.CLOCKED_IN,
                                            clockInTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                                        )
                                    } else shift
                                }
                            )
                        )
                    }
                }
            }
            is WelcomeAction.ClockOut -> {
                viewModelScope.launch {
                    // Update the shift status to COMPLETED and set clockOutTime
                    _state.update { currentState ->
                        currentState.copy(
                            welcomeState = currentState.welcomeState.copy(
                                todayShift = currentState.welcomeState.todayShift?.let { shift ->
                                    if (shift.id == action.shiftId) {
                                        shift.copy(
                                            status = ShiftStatus.COMPLETED,
                                            clockOutTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                                        )
                                    } else shift
                                }
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
            is WeeklyScheduleAction.NavigateToNextWeek -> {
                _state.update { currentState ->
                    currentState.copy(
                        weeklyScheduleState = currentState.weeklyScheduleState.copy(
                            currentWeekStart = currentState.weeklyScheduleState.currentWeekStart.plus(7, DateTimeUnit.DAY)
                        )
                    )
                }
                // Reload schedule for the new week
                onAction(HomeAction.WeeklyScheduleScreenAction(WeeklyScheduleAction.LoadWeeklySchedule))
            }
            is WeeklyScheduleAction.NavigateToPreviousWeek -> {
                _state.update { currentState ->
                    currentState.copy(
                        weeklyScheduleState = currentState.weeklyScheduleState.copy(
                            currentWeekStart = currentState.weeklyScheduleState.currentWeekStart.minus(7, DateTimeUnit.DAY)
                        )
                    )
                }
                // Reload schedule for the new week
                onAction(HomeAction.WeeklyScheduleScreenAction(WeeklyScheduleAction.LoadWeeklySchedule))
            }
            is WeeklyScheduleAction.NavigateToCurrentWeek -> {
                _state.update { currentState ->
                    currentState.copy(
                        weeklyScheduleState = currentState.weeklyScheduleState.copy(
                            currentWeekStart = Clock.System.now().toLocalDateTime(TimeZone.UTC).date,
                            selectedDay = Clock.System.now().toLocalDateTime(TimeZone.UTC).date.dayOfWeek
                        )
                    )
                }
                // Reload schedule for the current week
                onAction(HomeAction.WeeklyScheduleScreenAction(WeeklyScheduleAction.LoadWeeklySchedule))
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
                            selectedDate = action.date,
                            showAvailabilityDialog = false
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
                                isLoading = false,
                                showAvailabilityDialog = false
                            )
                        )
                    }
                }
            }
            is CalendarAction.ShowAvailabilityDialog -> {
                _state.update {
                    it.copy(
                        calendarState = it.calendarState.copy(
                            showAvailabilityDialog = true
                        )
                    )
                }
            }
            is CalendarAction.HideAvailabilityDialog -> {
                _state.update {
                    it.copy(
                        calendarState = it.calendarState.copy(
                            showAvailabilityDialog = false
                        )
                    )
                }
            }
            is CalendarAction.NavigateToNextMonth -> {
                val currentDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
                val targetDate = _state.value.calendarState.currentMonth.plus(1, DateTimeUnit.MONTH)
                val maxDate = currentDate.plus(3, DateTimeUnit.MONTH)
                
                // Compare years and months separately to handle year transitions
                val isWithinRange = (targetDate.year < maxDate.year) || 
                    (targetDate.year == maxDate.year && targetDate.month.ordinal <= maxDate.month.ordinal)
                
                if (isWithinRange) {
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
                val minDate = currentDate.minus(3, DateTimeUnit.MONTH)
                
                // Compare years and months separately to handle year transitions
                val isWithinRange = (targetDate.year > minDate.year) || 
                    (targetDate.year == minDate.year && targetDate.month.ordinal >= minDate.month.ordinal)
                
                if (isWithinRange) {
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
}

sealed interface HomeAction {
    data class Navigate(val screen: HomeScreen) : HomeAction
    data class WelcomeScreenAction(val action: WelcomeAction) : HomeAction
    data class WeeklyScheduleScreenAction(val action: WeeklyScheduleAction) : HomeAction
    data class CalendarScreenAction(val action: CalendarAction) : HomeAction
    data class SearchScreenAction(val action: SearchAction) : HomeAction
    data class BusinessScreenAction(val action: BusinessAction) : HomeAction
}

data class HomeState(
    val currentScreen: HomeScreen = HomeScreen.Welcome,
    val welcomeState: WelcomeState = WelcomeState(),
    val weeklyScheduleState: WeeklyScheduleState = WeeklyScheduleState(),
    val calendarState: CalendarState = CalendarState(
        currentMonth = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
    ),
    val searchState: SearchState = SearchState(),
    val businessState: BusinessState = BusinessState()
) 