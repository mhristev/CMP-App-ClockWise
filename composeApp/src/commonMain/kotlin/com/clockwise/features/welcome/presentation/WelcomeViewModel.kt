package com.clockwise.features.welcome.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.features.shift.schedule.domain.ShiftRepository
import com.clockwise.features.shift.schedule.presentation.Shift
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class WelcomeViewModel(
    private val shiftRepository: ShiftRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WelcomeState())
    val state: StateFlow<WelcomeState> = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: WelcomeAction) {
        when (action) {
            is WelcomeAction.LoadUpcomingShifts -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                    
                    try {
                        // Fetch upcoming shifts from the API
                        val upcomingShiftsDto = shiftRepository.getUpcomingShiftsForCurrentUser()
                        
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
                            Shift(
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
                                todayShift = todayShift,
                                upcomingShifts = upcomingShifts,
                                isLoading = false
                            )
                        }
                    } catch (e: Exception) {
                        println("Error loading shifts: ${e.message}")
                        e.printStackTrace()
                        
                        // Update state with error
                        _state.update {
                            it.copy(
                                isLoading = false
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
                            todayShift = currentState.todayShift?.let { shift ->
                                if (shift.id == action.shiftId) {
                                    shift.copy(
                                        status = ShiftStatus.CLOCKED_IN,
                                        clockInTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                                    )
                                } else shift
                            }
                        )
                    }
                }
            }
            is WelcomeAction.ClockOut -> {
                viewModelScope.launch {
                    // Update the shift status to COMPLETED and set clockOutTime
                    _state.update { currentState ->
                        currentState.copy(
                            todayShift = currentState.todayShift?.let { shift ->
                                if (shift.id == action.shiftId) {
                                    shift.copy(
                                        status = ShiftStatus.COMPLETED,
                                        clockOutTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                                    )
                                } else shift
                            }
                        )
                    }
                }
            }
        }
    }
} 