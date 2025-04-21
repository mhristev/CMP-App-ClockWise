package com.clockwise.user.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.service.UserService
import com.clockwise.service.ShiftService
import com.clockwise.service.AvailabilityService
import com.clockwise.user.domain.UserRole
import com.clockwise.user.domain.AccessControl
import com.clockwise.user.presentation.home.calendar.CalendarAction
import com.clockwise.user.presentation.home.calendar.CalendarState
import com.clockwise.user.presentation.home.profile.ProfileAction
import com.clockwise.user.presentation.home.profile.ProfileState
import com.clockwise.user.presentation.home.schedule.Shift
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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber
import com.clockwise.navigation.NavigationRoutes
import com.clockwise.user.presentation.home.HomeScreen
import com.clockwise.user.presentation.home.business.BusinessAction
import com.clockwise.user.presentation.home.business.BusinessState
import com.clockwise.user.presentation.home.business.BusinessViewModel

// Helper function to calculate the first day (Monday) of the week containing the given date
fun getWeekStartDate(date: LocalDate): LocalDate {
    // In ISO-8601, Monday is 1 and Sunday is 7
    val dayOfWeek = date.dayOfWeek.isoDayNumber
    // Calculate how many days to go back to reach Monday
    val daysToSubtract = dayOfWeek - 1
    return date.minus(daysToSubtract, DateTimeUnit.DAY)
}

class HomeViewModel(
    private val userService: UserService,
    private val shiftService: ShiftService,
    private val availabilityService: AvailabilityService
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.NavigateToScreen -> {
                _state.update {
                    it.copy(currentScreen = action.screen)
                }
            }
            is HomeAction.WelcomeScreenAction -> handleWelcomeAction(action.action)
            is HomeAction.WeeklyScheduleScreenAction -> handleWeeklyScheduleAction(action.action)
            is HomeAction.CalendarScreenAction -> handleCalendarAction(action.action)
            is HomeAction.SearchScreenAction -> handleSearchAction(action.action)
            is HomeAction.BusinessScreenAction -> handleBusinessAction(action.action)
            is HomeAction.ProfileScreenAction -> handleProfileAction(action.action)
        }
    }

    private fun handleWelcomeAction(action: WelcomeAction) {
        when (action) {
            is WelcomeAction.LoadUpcomingShifts -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            welcomeState = it.welcomeState.copy(
                                isLoading = true
                            )
                        )
                    }
                    
                    try {
                        // Fetch upcoming shifts from the API
                        val upcomingShiftsDto = shiftService.getUpcomingShiftsForCurrentUser()
                        
                        // Find the current day to determine today's shift
                        val now = Clock.System.now()
                        val today = now.toLocalDateTime(TimeZone.UTC).date
                        
                        // Convert DTOs to model objects
                        val shifts = upcomingShiftsDto.map { shiftDto ->
                            // Extract date and time components
                            val year = shiftDto.startTime[0]
                            val month = shiftDto.startTime[1]
                            val day = shiftDto.startTime[2]
                            val startHour = shiftDto.startTime[3]
                            val startMinute = shiftDto.startTime[4]
                            
                            val endHour = shiftDto.endTime[3]
                            val endMinute = shiftDto.endTime[4]
                            
                            // Create the start and end times
                            val startTime = kotlinx.datetime.LocalDateTime(
                                year = year,
                                monthNumber = month,
                                dayOfMonth = day,
                                hour = startHour,
                                minute = startMinute
                            )
                            
                            val endTime = kotlinx.datetime.LocalDateTime(
                                year = shiftDto.endTime[0],
                                monthNumber = shiftDto.endTime[1],
                                dayOfMonth = shiftDto.endTime[2],
                                hour = endHour,
                                minute = endMinute
                            )
                            
                            // Create the shift
                            com.clockwise.user.presentation.home.welcome.Shift(
                                id = shiftDto.id.hashCode(),
                                date = startTime,
                                startTime = startTime,
                                endTime = endTime,
                                location = shiftDto.position ?: "General Staff",
                                status = ShiftStatus.SCHEDULED
                            )
                        }
                        
                        // Separate today's shift from upcoming shifts
                        val todayShift = shifts.find { shift -> 
                            shift.date.date == today
                        }
                        
                        val upcomingShifts = shifts.filter { shift ->
                            shift.date.date > today
                        }

                        println("Today: $today, Today's shift: ${todayShift?.date?.date}, Upcoming shifts: ${upcomingShifts.size}")
                        
                        // Update state with the shifts
                        _state.update {
                            it.copy(
                                welcomeState = it.welcomeState.copy(
                                    todayShift = todayShift,
                                    upcomingShifts = upcomingShifts,
                                    isLoading = false
                                )
                            )
                        }
                    } catch (e: Exception) {
                        println("Error loading shifts: ${e.message}")
                        e.printStackTrace()
                        
                        // Update state with error
                        _state.update {
                            it.copy(
                                welcomeState = it.welcomeState.copy(
                                    isLoading = false
                                )
                            )
                        }
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
                    _state.update {
                        it.copy(
                            weeklyScheduleState = it.weeklyScheduleState.copy(
                                isLoading = true
                            )
                        )
                    }
                    
                    // Fetch shifts for the entire week
                    val weekStart = _state.value.weeklyScheduleState.currentWeekStart
                    val shiftsForWeek = shiftService.getShiftsForWeek(weekStart)
                    
                    // Group shifts by day of week
                    val shiftsByDay = mutableMapOf<DayOfWeek, MutableList<Shift>>()
                    
                    // Initialize empty lists for each day of the week
                    DayOfWeek.values().forEach { day ->
                        shiftsByDay[day] = mutableListOf()
                    }
                    
                    // Organize shifts by day
                    shiftsForWeek.forEach { shiftDto ->
                        try {
                            // Extract date components from the array
                            val year = shiftDto.startTime[0]
                            val month = shiftDto.startTime[1]
                            val day = shiftDto.startTime[2]
                            val hour = shiftDto.startTime[3]
                            val minute = shiftDto.startTime[4]
                            
                            // Create LocalDate from components
                            val date = LocalDate(year, month, day)
                            
                            // Format time as HH:MM
                            val startTime = String.format("%02d:%02d", hour, minute)
                            
                            // Get end time components and format
                            val endHour = shiftDto.endTime[3]
                            val endMinute = shiftDto.endTime[4]
                            val endTime = String.format("%02d:%02d", endHour, endMinute)
                            
                            // Get day of week
                            val dayOfWeek = date.dayOfWeek
                            
                            // Create shift object
                            val shift = Shift(
                                date = date,
                                startTime = startTime,
                                endTime = endTime,
                                position = shiftDto.position ?: "General Staff",
                                employees = listOf(shiftDto.employeeId) // You might want to get actual names later
                            )
                            
                            // Add to the appropriate day
                            shiftsByDay[dayOfWeek]?.add(shift)
                            
                        } catch (e: Exception) {
                            println("Error processing shift: ${e.message}")
                        }
                    }
                    
                    // Update state with the organized shifts
                    _state.update {
                        it.copy(
                            weeklyScheduleState = it.weeklyScheduleState.copy(
                                weeklySchedule = shiftsByDay.mapValues { entry -> entry.value },
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
                
                // Optionally fetch shifts just for the selected day
                viewModelScope.launch {
                    val selectedDay = _state.value.weeklyScheduleState.selectedDay ?: return@launch
                    val weekStart = _state.value.weeklyScheduleState.currentWeekStart
                    
                    // Calculate the date for the selected day
                    val daysToAdd = selectedDay.isoDayNumber - DayOfWeek.MONDAY.isoDayNumber
                    val selectedDate = weekStart.plus(daysToAdd, DateTimeUnit.DAY)
                    
                    // Fetch shifts for just this day if needed
                    // This is optional since we're already loading the whole week
                    // val shiftsForDay = shiftService.getShiftsForDay(selectedDate)
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
                val today = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
                _state.update { currentState ->
                    currentState.copy(
                        weeklyScheduleState = currentState.weeklyScheduleState.copy(
                            currentWeekStart = getWeekStartDate(today),
                            selectedDay = today.dayOfWeek
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
                    _state.update {
                        it.copy(
                            calendarState = it.calendarState.copy(
                                isLoading = true
                            )
                        )
                    }
                    
                    try {
                        // Load availabilities from the API
                        val availabilities = availabilityService.getUserAvailabilities()
                        
                        // Convert to a map of LocalDate -> Pair<StartTime, EndTime>
                        val availabilityMap = mutableMapOf<LocalDate, Pair<String, String>>()
                        val availabilityIdMap = mutableMapOf<LocalDate, String>()
                        
                        availabilities.forEach { availability ->
                            // Extract date components from the startTime array
                            val year = availability.startTime[0]
                            val month = availability.startTime[1]
                            val day = availability.startTime[2]
                            val startHour = availability.startTime[3]
                            val startMinute = availability.startTime[4]
                            
                            val endHour = availability.endTime[3]
                            val endMinute = availability.endTime[4]
                            
                            // Create the date and format the times
                            val date = kotlinx.datetime.LocalDate(year, month, day)
                            val startTime = String.format("%02d:%02d", startHour, startMinute)
                            val endTime = String.format("%02d:%02d", endHour, endMinute)
                            
                            availabilityMap[date] = Pair(startTime, endTime)
                            
                            // Store the availability ID
                            availability.id?.let { id ->
                                availabilityIdMap[date] = id
                            }
                        }
                        
                        _state.update {
                            it.copy(
                                calendarState = it.calendarState.copy(
                                    monthlySchedule = availabilityMap,
                                    availabilityIdMap = availabilityIdMap,
                                    isLoading = false
                                )
                            )
                        }
                    } catch (e: Exception) {
                        println("Error loading availabilities: ${e.message}")
                        e.printStackTrace()
                        
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
                    _state.update {
                        it.copy(
                            calendarState = it.calendarState.copy(
                                isLoading = true
                            )
                        )
                    }
                    
                    try {
                        // Check if we're updating an existing availability or creating a new one
                        val existingAvailabilityId = _state.value.calendarState.availabilityIdMap[action.date]
                        val availabilityDto = if (existingAvailabilityId != null) {
                            // Update existing availability
                            println("Updating existing availability with ID: $existingAvailabilityId")
                            availabilityService.updateAvailability(
                                id = existingAvailabilityId,
                                date = action.date,
                                startTimeString = action.startTime,
                                endTimeString = action.endTime
                            )
                        } else {
                            // Create new availability
                            println("Creating new availability")
                            availabilityService.createAvailability(
                                date = action.date,
                                startTimeString = action.startTime,
                                endTimeString = action.endTime
                            )
                        }
                        
                        // Update local state
                        _state.update { state ->
                            val updatedSchedule = state.calendarState.monthlySchedule.toMutableMap()
                            updatedSchedule[action.date] = Pair(action.startTime, action.endTime)
                            
                            val updatedIdMap = state.calendarState.availabilityIdMap.toMutableMap()
                            availabilityDto.id?.let { id ->
                                updatedIdMap[action.date] = id
                            }
                            
                            state.copy(
                                calendarState = state.calendarState.copy(
                                    monthlySchedule = updatedSchedule,
                                    availabilityIdMap = updatedIdMap,
                                    isLoading = false,
                                    showAvailabilityDialog = false
                                )
                            )
                        }
                    } catch (e: Exception) {
                        println("Error submitting availability: ${e.message}")
                        e.printStackTrace()
                        
                        // Update state with error
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
            }
            is CalendarAction.EditAvailability -> {
                _state.update {
                    it.copy(
                        calendarState = it.calendarState.copy(
                            showAvailabilityDialog = true
                        )
                    )
                }
                
                // Handle the edit the same way as a new creation, the update happens when SetAvailability is called
                // This relies on the fact that the date won't change between EditAvailability and SetAvailability
            }
            is CalendarAction.DeleteAvailability -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            calendarState = it.calendarState.copy(
                                isLoading = true
                            )
                        )
                    }
                    
                    try {
                        val availabilityId = _state.value.calendarState.availabilityIdMap[action.date]
                        
                        if (availabilityId != null) {
                            // Delete availability from the backend
                            val success = availabilityService.deleteAvailability(availabilityId)
                            
                            if (success) {
                                // Update local state
                                _state.update { state ->
                                    val updatedSchedule = state.calendarState.monthlySchedule.toMutableMap()
                                    updatedSchedule.remove(action.date)
                                    
                                    val updatedIdMap = state.calendarState.availabilityIdMap.toMutableMap()
                                    updatedIdMap.remove(action.date)
                                    
                                    state.copy(
                                        calendarState = state.calendarState.copy(
                                            monthlySchedule = updatedSchedule,
                                            availabilityIdMap = updatedIdMap,
                                            isLoading = false
                                        )
                                    )
                                }
                            } else {
                                println("Error deleting availability: Backend reported failure")
                                _state.update {
                                    it.copy(
                                        calendarState = it.calendarState.copy(
                                            isLoading = false
                                        )
                                    )
                                }
                            }
                        } else {
                            println("Error deleting availability: No ID found for date")
                            _state.update {
                                it.copy(
                                    calendarState = it.calendarState.copy(
                                        isLoading = false
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        println("Error deleting availability: ${e.message}")
                        e.printStackTrace()
                        
                        _state.update {
                            it.copy(
                                calendarState = it.calendarState.copy(
                                    isLoading = false
                                )
                            )
                        }
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
            is CalendarAction.ShowDeleteConfirmation -> {
                _state.update {
                    it.copy(
                        calendarState = it.calendarState.copy(
                            showDeleteConfirmationDialog = true
                        )
                    )
                }
            }
            is CalendarAction.HideDeleteConfirmation -> {
                _state.update {
                    it.copy(
                        calendarState = it.calendarState.copy(
                            showDeleteConfirmationDialog = false
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

    private fun handleSearchAction(action: SearchAction) {
        when (action) {
            // Handle each specific SearchAction case
            is SearchAction.Search -> { /* Implementation */ }
            is SearchAction.SearchSuccess -> { /* Implementation */ }
            is SearchAction.SearchError -> { /* Implementation */ }
            is SearchAction.AddUserToBusinessUnit -> { /* Implementation */ }
            is SearchAction.AddUserSuccess -> { /* Implementation */ }
            is SearchAction.AddUserError -> { /* Implementation */ }
            is SearchAction.ClearMessages -> { /* Implementation */ }
            else -> { /* Catch any other actions */ }
        }
    }
    
    private fun handleBusinessAction(action: BusinessAction) {
        when (action) {
            // Handle each specific BusinessAction case
            is BusinessAction.LoadBusinessData -> { /* Implementation */ }
            is BusinessAction.BusinessDataLoaded -> { /* Implementation */ }
            is BusinessAction.EmployeesLoaded -> { /* Implementation */ }
            is BusinessAction.Error -> { /* Implementation */ }
            is BusinessAction.SwitchView -> { /* Implementation */ }
            else -> { /* Catch any other actions */ }
        }
    }

    private fun handleProfileAction(action: ProfileAction) {
        when (action) {
            // Handle each specific ProfileAction case
            is ProfileAction.LoadUserProfile -> { /* Implementation */ }
            is ProfileAction.UpdateProfile -> { /* Implementation */ }
            is ProfileAction.Logout -> { /* Implementation */ }
            else -> { /* Catch any other actions */ }
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