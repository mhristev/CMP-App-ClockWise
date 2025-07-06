package com.clockwise.features.shift.presentation.welcome_shifts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.core.TimeProvider
import com.clockwise.features.shift.domain.model.Shift
import com.clockwise.features.shift.domain.model.ShiftStatus
import com.clockwise.features.shift.domain.model.WorkSession
import com.clockwise.features.shift.domain.model.WorkSessionStatus
import com.clockwise.features.shift.domain.repositories.ShiftRepository
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
                            isLoading = true,
                            error = null
                        )
                    }

                    // Fetch upcoming shifts from the API
                    shiftRepository.getUpcomingShifts().collect { result ->
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
                    // Show loading indicator
                    _state.update { it.copy(isLoading = true) }

                    // Call the repository to perform clock-in
                    shiftRepository.clockIn(
                        shiftId = action.shiftId
                    ).collect { result ->
                        result.onSuccess {
                            // Reload shifts to get updated status
                            onAction(WelcomeAction.LoadUpcomingShifts)
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
            }
            is WelcomeAction.ClockOut -> {
                viewModelScope.launch {
                    // Show loading indicator
                    _state.update { it.copy(isLoading = true) }

                    // Call the repository to perform clock-out
                    shiftRepository.clockOut(
                        shiftId = action.shiftId
                    ).collect { result ->
                        result.onSuccess {
                            // Reload shifts to get updated status
                            onAction(WelcomeAction.LoadUpcomingShifts)
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
            is WelcomeAction.SaveNote -> saveNote(action.workSessionId, action.note)
        }
    }

    private fun saveNote(workSessionId: String, note: String) {
        viewModelScope.launch {
            shiftRepository.saveSessionNote(workSessionId, note)
        }
    }
} 