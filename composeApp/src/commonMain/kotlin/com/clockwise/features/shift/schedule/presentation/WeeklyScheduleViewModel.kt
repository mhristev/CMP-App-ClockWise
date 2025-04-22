package com.clockwise.features.shift.schedule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.core.TimeProvider
import com.clockwise.core.util.formatTimeString
import com.clockwise.features.shift.schedule.domain.ShiftRepository
import com.clockwise.features.shift.schedule.presentation.Shift
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.isoDayNumber

// Helper function to calculate the first day (Monday) of the week containing the given date
fun getWeekStartDate(date: LocalDate): LocalDate {
    // In ISO-8601, Monday is 1 and Sunday is 7
    val dayOfWeek = date.dayOfWeek.isoDayNumber
    // Calculate how many days to go back to reach Monday
    val daysToSubtract = dayOfWeek - 1
    return date.minus(daysToSubtract, DateTimeUnit.DAY)
}

class WeeklyScheduleViewModel(
    private val shiftRepository: ShiftRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WeeklyScheduleState(
        currentWeekStart = getWeekStartDate(TimeProvider.getCurrentLocalDate())
    ))
    val state: StateFlow<WeeklyScheduleState> = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: WeeklyScheduleAction) {
        when (action) {
            is WeeklyScheduleAction.LoadWeeklySchedule -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                    
                    // Fetch shifts for the entire week
                    val weekStart = _state.value.currentWeekStart
                    val shiftsForWeek = shiftRepository.getShiftsForWeek(weekStart)
                    
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
                            val startTime = formatTimeString(hour, minute)
                            
                            // Get end time components and format
                            val endHour = shiftDto.endTime[3]
                            val endMinute = shiftDto.endTime[4]
                            val endTime = formatTimeString(endHour, endMinute)
                            
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
                            weeklySchedule = shiftsByDay.mapValues { entry -> entry.value },
                            isLoading = false
                        )
                    }
                }
            }
            is WeeklyScheduleAction.SelectDay -> {
                _state.update {
                    it.copy(
                        selectedDay = action.day
                    )
                }
                
                // Optionally fetch shifts just for the selected day
                viewModelScope.launch {
                    val selectedDay = _state.value.selectedDay ?: return@launch
                    val weekStart = _state.value.currentWeekStart
                    
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
                        currentWeekStart = currentState.currentWeekStart.plus(7, DateTimeUnit.DAY)
                    )
                }
                // Reload schedule for the new week
                onAction(WeeklyScheduleAction.LoadWeeklySchedule)
            }
            is WeeklyScheduleAction.NavigateToPreviousWeek -> {
                _state.update { currentState ->
                    currentState.copy(
                        currentWeekStart = currentState.currentWeekStart.minus(7, DateTimeUnit.DAY)
                    )
                }
                // Reload schedule for the new week
                onAction(WeeklyScheduleAction.LoadWeeklySchedule)
            }
            is WeeklyScheduleAction.NavigateToCurrentWeek -> {
                val today = TimeProvider.getCurrentLocalDate()
                _state.update { currentState ->
                    currentState.copy(
                        currentWeekStart = getWeekStartDate(today),
                        selectedDay = today.dayOfWeek
                    )
                }
                // Reload schedule for the current week
                onAction(WeeklyScheduleAction.LoadWeeklySchedule)
            }
        }
    }
} 