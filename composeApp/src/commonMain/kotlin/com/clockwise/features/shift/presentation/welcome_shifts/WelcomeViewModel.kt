package com.clockwise.features.shift.presentation.welcome_shifts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.core.TimeProvider
import com.clockwise.features.shift.data.repository.ShiftRepository
import com.clockwise.features.shift.data.repository.WorkSessionRepository
import com.clockwise.features.shift.domain.model.Shift
import com.clockwise.features.shift.domain.model.ShiftStatus
import com.clockwise.features.shift.domain.model.WorkSessionStatus
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import com.clockwise.features.shift.domain.model.WorkSession

class WelcomeViewModel(
    private val shiftRepository: ShiftRepository,
    private val workSessionRepository: WorkSessionRepository
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
                            isLoading = true,
                            error = null
                        )
                    }
                    
                    // Fetch upcoming shifts from the API
                    shiftRepository.getUpcomingShiftsForCurrentUser().collect { result ->
                        result.onSuccess { shiftDtos ->
                            // Find the current day to determine today's shift
                            val today = TimeProvider.getCurrentLocalDate()
                            
                            // Convert DTOs to model objects
                            val shifts = shiftDtos.map { shiftDto ->
                                // Convert timestamps to LocalDateTime
                                val startTime = TimeProvider.epochSecondsToLocalDateTime(shiftDto.startTime.toDouble())
                                val endTime = TimeProvider.epochSecondsToLocalDateTime(shiftDto.endTime.toDouble())
                                
                                val workSession = shiftDto.workSession?.let { wsDto ->
                                    WorkSession(
                                        id = wsDto.id,
                                        userId = wsDto.userId,
                                        shiftId = wsDto.shiftId,
                                        clockInTime = TimeProvider.epochSecondsToLocalDateTime(wsDto.clockInTime),
                                        clockOutTime = wsDto.clockOutTime?.let { TimeProvider.epochSecondsToLocalDateTime(it) },
                                        totalMinutes = wsDto.totalMinutes,
                                        status = WorkSessionStatus.fromString(wsDto.status)
                                    )
                                }

                                // Create the shift
                                Shift(
                                    id = shiftDto.id,
                                    startTime = startTime,
                                    endTime = endTime,
                                    position = shiftDto.position ?: "General Staff",
                                    employeeId = shiftDto.employeeId,
                                    workSession = workSession,
                                    status = workSession?.let {
                                        when(it.status) {
                                            WorkSessionStatus.ACTIVE -> ShiftStatus.CLOCKED_IN
                                            WorkSessionStatus.COMPLETED -> ShiftStatus.COMPLETED
                                            else -> ShiftStatus.SCHEDULED
                                        }
                                    } ?: ShiftStatus.SCHEDULED,
                                    clockInTime = workSession?.clockInTime,
                                    clockOutTime = workSession?.clockOutTime
                                )
                            }
                            
                            // Separate today's shift from upcoming shifts
                            val todayShift = shifts.find { shift -> 
                                shift.startTime.date == today
                            }
                            
                            val upcomingShifts = shifts.filter { shift ->
                                shift.startTime.date > today
                            }

                            println("Today: $today, Today's shift: ${todayShift?.startTime?.date}, Upcoming shifts: ${upcomingShifts.size}")
                            
                            // Update state with the shifts
                            _state.update {
                                it.copy(
                                    todayShift = todayShift,
                                    upcomingShifts = upcomingShifts,
                                    isLoading = false
                                )
                            }
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
            is WelcomeAction.ClockIn -> {
                viewModelScope.launch {
                    // Get the current shift
                    val currentShift = _state.value.todayShift ?: return@launch
                    
                    // Show loading indicator
                    _state.update { it.copy(isLoading = true) }
                    
                    // Call the repository to perform clock-in
                    workSessionRepository.clockIn(
                        userId = currentShift.employeeId,
                        shiftId = action.shiftId
                    ).onSuccess { workSession ->
                        // Update the shift status with data from the response
                        _state.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                todayShift = currentState.todayShift?.let { shift ->
                                    if (shift.id == action.shiftId) {
                                        // Make sure to properly set the CLOCKED_IN status based on workSession.status
                                        val newStatus = when (workSession.status) {
                                            WorkSessionStatus.ACTIVE -> ShiftStatus.CLOCKED_IN
                                            WorkSessionStatus.COMPLETED -> ShiftStatus.COMPLETED
                                            WorkSessionStatus.CANCELLED -> ShiftStatus.SCHEDULED
                                        }
                                        shift.copy(
                                            status = newStatus,
                                            clockInTime = workSession.clockInTime,
                                            workSession = workSession
                                        )
                                    } else shift
                                }
                            )
                        }
                    }.onError { error ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to clock in: ${error.name}"
                            )
                        }
                    }
                }
            }
            is WelcomeAction.ClockOut -> {
                viewModelScope.launch {
                    // Get the current shift
                    val currentShift = _state.value.todayShift ?: return@launch
                    
                    // Show loading indicator
                    _state.update { it.copy(isLoading = true) }
                    
                    // Call the repository to perform clock-out
                    workSessionRepository.clockOut(
                        userId = currentShift.employeeId,
                        shiftId = action.shiftId
                    ).onSuccess { workSession ->
                        // Update the shift status with data from the response
                        _state.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                todayShift = currentState.todayShift?.let { shift ->
                                    if (shift.id == action.shiftId) {
                                        // Map the work session status to shift status
                                        val newStatus = when (workSession.status) {
                                            WorkSessionStatus.ACTIVE -> ShiftStatus.CLOCKED_IN
                                            WorkSessionStatus.COMPLETED -> ShiftStatus.COMPLETED
                                            WorkSessionStatus.CANCELLED -> ShiftStatus.SCHEDULED
                                        }
                                        shift.copy(
                                            status = newStatus,
                                            clockOutTime = workSession.clockOutTime
                                        )
                                    } else shift
                                }
                            )
                        }
                    }.onError { error ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to clock out: ${error.name}"
                            )
                        }
                    }
                }
            }
        }
    }
} 