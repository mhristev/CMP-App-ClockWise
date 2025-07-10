package com.clockwise.features.shift.presentation.welcome_shifts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.core.TimeProvider
import com.clockwise.features.shift.domain.model.Shift
import com.clockwise.features.shift.domain.model.ShiftStatus
import com.clockwise.features.shift.domain.model.WorkSession
import com.clockwise.features.shift.domain.model.WorkSessionStatus
import com.clockwise.features.shift.domain.model.SessionNote
import com.clockwise.features.shift.domain.repositories.ShiftRepository
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.domain.DataError
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

    private fun getErrorMessage(error: DataError.Remote, operation: String): String {
        return when (error) {
            DataError.Remote.SCHEDULE_NOT_PUBLISHED -> {
                "No published schedule available. Please check back later or contact your manager."
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
                                val startTime = TimeProvider.epochSecondsToLocalDateTime(shiftDto.startTime)
                                val endTime = TimeProvider.epochSecondsToLocalDateTime(shiftDto.endTime)

                                val workSession = shiftDto.workSession?.let { wsDto ->
                                    val sessionNote = wsDto.sessionNote?.let { noteDto ->
                                        SessionNote(
                                            id = noteDto.id,
                                            workSessionId = noteDto.workSessionId,
                                            content = noteDto.content,
                                            createdAt = TimeProvider.epochSecondsToLocalDateTime(noteDto.createdAt)
                                        )
                                    }
                                    
                                    WorkSession(
                                        id = wsDto.id,
                                        userId = wsDto.userId,
                                        shiftId = wsDto.shiftId,
                                        clockInTime = wsDto.clockInTime?.let { TimeProvider.epochSecondsToLocalDateTime(it) },
                                        clockOutTime = wsDto.clockOutTime?.let { TimeProvider.epochSecondsToLocalDateTime(it) },
                                        totalMinutes = wsDto.totalMinutes,
                                        status = WorkSessionStatus.fromString(wsDto.status),
                                        sessionNote = sessionNote
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
                                            WorkSessionStatus.CREATED -> ShiftStatus.SCHEDULED
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
                                    isLoading = false,
                                    // Initialize session notes from backend data
                                    sessionNotes = shifts.mapNotNull { shift ->
                                        shift.workSession?.let { workSession ->
                                            workSession.id to (workSession.sessionNote?.content ?: "")
                                        }
                                    }.toMap()
                                )
                            }
                        }.onError { error ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = getErrorMessage(error, "load shifts")
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
                                    error = getErrorMessage(error, "clock in")
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
                            // Clear session notes for this shift's work session on successful clock out
                            val currentState = _state.value
                            val shiftToClockOut = currentState.todayShift?.takeIf { it.id == action.shiftId }
                            val workSessionId = shiftToClockOut?.workSession?.id
                            
                            val updatedSessionNotes = if (workSessionId != null) {
                                currentState.sessionNotes.filterKeys { it != workSessionId }
                            } else {
                                currentState.sessionNotes
                            }
                            
                            _state.update { 
                                it.copy(
                                    isLoading = false,
                                    sessionNotes = updatedSessionNotes
                                ) 
                            }
                            
                            // Reload shifts to get updated status
                            onAction(WelcomeAction.LoadUpcomingShifts)
                        }.onError { error ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = getErrorMessage(error, "clock out")
                                )
                            }
                        }
                    }
                }
            }
            is WelcomeAction.ShowClockOutModal -> {
                _state.update {
                    it.copy(
                        showClockOutModal = true,
                        clockOutModalShiftId = action.shiftId,
                        clockOutModalWorkSessionId = action.workSessionId,
                        clockOutNote = ""
                    )
                }
            }
            is WelcomeAction.HideClockOutModal -> {
                _state.update {
                    it.copy(
                        showClockOutModal = false,
                        clockOutModalShiftId = null,
                        clockOutModalWorkSessionId = null,
                        clockOutNote = ""
                    )
                }
            }
            is WelcomeAction.ClockOutWithNote -> {
                viewModelScope.launch {
                    // Show loading indicator
                    _state.update { it.copy(isLoading = true) }
                    
                    // Save note first if provided and workSessionId exists
                    if (action.note.isNotBlank() && action.workSessionId != null) {
                        shiftRepository.saveSessionNote(action.workSessionId, action.note).collect { noteResult ->
                            noteResult.onError { error ->
                                println("Failed to save session note: ${getErrorMessage(error, "save note")}")
                                // Continue with clock out even if note save fails
                            }
                        }
                    }
                    
                    // Then perform clock-out
                    shiftRepository.clockOut(
                        shiftId = action.shiftId
                    ).collect { result ->
                        result.onSuccess {
                            // Clear session notes for this shift's work session on successful clock out
                            val currentState = _state.value
                            val updatedSessionNotes = if (action.workSessionId != null) {
                                currentState.sessionNotes.filterKeys { it != action.workSessionId }
                            } else {
                                currentState.sessionNotes
                            }
                            
                            _state.update { 
                                it.copy(
                                    isLoading = false,
                                    sessionNotes = updatedSessionNotes,
                                    showClockOutModal = false,
                                    clockOutModalShiftId = null,
                                    clockOutModalWorkSessionId = null,
                                    clockOutNote = ""
                                ) 
                            }
                            
                            // Reload shifts to get updated status
                            onAction(WelcomeAction.LoadUpcomingShifts)
                        }.onError { error ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = getErrorMessage(error, "clock out with note")
                                )
                            }
                        }
                    }
                }
            }
            is WelcomeAction.SaveNote -> saveNote(action.workSessionId, action.note)
            is WelcomeAction.UpdateSessionNote -> {
                _state.update { currentState ->
                    currentState.copy(
                        sessionNotes = currentState.sessionNotes.toMutableMap().apply {
                            put(action.workSessionId, action.note)
                        }
                    )
                }
            }
            is WelcomeAction.UpdateClockOutNote -> {
                _state.update {
                    it.copy(clockOutNote = action.note)
                }
            }
        }
    }

    private fun saveNote(workSessionId: String, note: String) {
        viewModelScope.launch {
            // Show saving state
            _state.update { it.copy(savingNoteForSession = workSessionId) }
            
            shiftRepository.saveSessionNote(workSessionId, note).collect { result ->
                result.onSuccess {
                    // Clear saving state on success
                    _state.update { it.copy(savingNoteForSession = null) }
                }.onError { error ->
                    // Clear saving state and show error
                    _state.update {
                        it.copy(
                            savingNoteForSession = null,
                            error = getErrorMessage(error, "save note")
                        )
                    }
                }
            }
        }
    }
} 