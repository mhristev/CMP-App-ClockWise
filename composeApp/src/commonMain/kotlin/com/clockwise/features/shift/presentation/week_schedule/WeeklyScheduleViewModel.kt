package com.clockwise.features.shift.presentation.week_schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.core.TimeProvider
import com.clockwise.features.shift.domain.repositories.ShiftRepository
import com.clockwise.features.shift.domain.model.Shift
import com.clockwise.core.util.getWeekStartDate
import com.clockwise.features.shift.data.dto.ShiftDto
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.domain.DataError
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

    private fun getErrorMessage(error: DataError.Remote, operation: String): String {
        return when (error) {
            DataError.Remote.SCHEDULE_NOT_PUBLISHED -> {
                "No published schedule available for this week. Please check back later or contact your manager."
            }
            DataError.Remote.NO_INTERNET -> {
                "No internet connection. Please check your network and try again."
            }
            DataError.Remote.SERVER -> {
                "Server error occurred. Please try again in a few minutes."
            }
            DataError.Remote.REQUEST_TIMEOUT -> {
                "Request timed out. Please check your connection and try again."
            }
            DataError.Remote.TOO_MANY_REQUESTS -> {
                "Too many requests. Please wait a moment and try again."
            }
            DataError.Remote.SERIALIZATION -> {
                "Data format error. Please try again or contact support."
            }
            else -> {
                "Failed to $operation. Please try again later."
            }
        }
    }

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
            
            // Fetch shifts for the entire week - pass LocalDate directly to avoid parsing errors
            val weekStart = _state.value.currentWeekStart
            shiftRepository.getShiftsForWeek(weekStart).collect { result ->
                result.onSuccess { shiftDtos ->
                    processShiftDtos(shiftDtos)
                }.onError { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = getErrorMessage(error, "load weekly schedule")
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
                // Convert epoch seconds to LocalDateTime
                val startTime = TimeProvider.epochSecondsToLocalDateTime(shiftDto.startTime)
                val endTime = TimeProvider.epochSecondsToLocalDateTime(shiftDto.endTime)
                
                // Get day of week
                val dayOfWeek = startTime.date.dayOfWeek
                
                // Create shift object
                val shift = Shift(
                    id = shiftDto.id,
                    startTime = startTime,
                    endTime = endTime,
                    position = shiftDto.position ?: "General Staff",
                    employeeId = shiftDto.employeeId,
                    userFirstName = shiftDto.userFirstName,
                    userLastName = shiftDto.userLastName
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