package com.clockwise.features.shift.presentation.week_schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.core.TimeProvider
import com.clockwise.features.shift.data.repository.ShiftRepository
import com.clockwise.features.shift.domain.model.Shift
import com.clockwise.core.util.getWeekStartDate
import com.clockwise.features.shift.data.dto.ShiftDto
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class WeeklyScheduleViewModel(
    private val shiftRepository: ShiftRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        WeeklyScheduleState(
        currentWeekStart = getWeekStartDate(TimeProvider.getCurrentLocalDate())
    )
    )
    
    val state: StateFlow<WeeklyScheduleState> = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: WeeklyScheduleAction) {
        when (action) {
            is WeeklyScheduleAction.LoadWeeklySchedule -> loadWeeklySchedule()
            is WeeklyScheduleAction.SelectDay -> selectDay(action.day)
            is WeeklyScheduleAction.NavigateToNextWeek -> navigateToNextWeek()
            is WeeklyScheduleAction.NavigateToPreviousWeek -> navigateToPreviousWeek()
            is WeeklyScheduleAction.NavigateToCurrentWeek -> navigateToCurrentWeek()
        }
    }
    
    private fun loadWeeklySchedule() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }
            
            // Fetch shifts for the entire week
            val weekStart = _state.value.currentWeekStart
            shiftRepository.getShiftsForWeek(weekStart).collect { result ->
                result.onSuccess { shiftDtos ->
                    processShiftDtos(shiftDtos)
                }.onError { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load shifts: ${error.name}"
                        )
                    }
                }
            }
        }
    }
    
    private fun processShiftDtos(shiftDtos: List<ShiftDto>) {
        // Group shifts by day of week
        val shiftsByDay = mutableMapOf<DayOfWeek, MutableList<Shift>>()
        
        // Initialize empty lists for each day of the week
        DayOfWeek.values().forEach { day ->
            shiftsByDay[day] = mutableListOf()
        }
        
        // Organize shifts by day
        shiftDtos.forEach { shiftDto ->
            try {
                // Extract date components from the array
                val year = shiftDto.startTime[0]
                val month = shiftDto.startTime[1]
                val day = shiftDto.startTime[2]
                val startHour = shiftDto.startTime[3]
                val startMinute = shiftDto.startTime[4]
                
                // Create LocalDate from components
                val date = LocalDate(year, month, day)
                
                // Get end time components
                val endHour = shiftDto.endTime[3]
                val endMinute = shiftDto.endTime[4]
                
                // Get day of week
                val dayOfWeek = date.dayOfWeek

                val startTime = LocalDateTime(
                    year = year,
                    monthNumber = month,
                    dayOfMonth = day,
                    hour = startHour,
                    minute = startMinute
                )

                val endTime = LocalDateTime(
                    year = shiftDto.endTime[0],
                    monthNumber = shiftDto.endTime[1],
                    dayOfMonth = shiftDto.endTime[2],
                    hour = endHour,
                    minute = endMinute
                )
                
                // Create shift object
                val shift = Shift(
                    id = shiftDto.id,
                    startTime = startTime,
                    endTime = endTime,
                    position = shiftDto.position ?: "General Staff",
                    employeeId = shiftDto.employeeId,
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
    
    private fun selectDay(day: DayOfWeek) {
        _state.update {
            it.copy(selectedDay = day)
        }
    }
    
    private fun navigateToNextWeek() {
        _state.update { currentState ->
            currentState.copy(
                currentWeekStart = currentState.currentWeekStart.plus(7, DateTimeUnit.DAY)
            )
        }
        loadWeeklySchedule()
    }
    
    private fun navigateToPreviousWeek() {
        _state.update { currentState ->
            currentState.copy(
                currentWeekStart = currentState.currentWeekStart.minus(7, DateTimeUnit.DAY)
            )
        }
        loadWeeklySchedule()
    }
    
    private fun navigateToCurrentWeek() {
        val today = TimeProvider.getCurrentLocalDate()
        _state.update { currentState ->
            currentState.copy(
                currentWeekStart = getWeekStartDate(today),
                selectedDay = today.dayOfWeek
            )
        }
        loadWeeklySchedule()
    }
} 