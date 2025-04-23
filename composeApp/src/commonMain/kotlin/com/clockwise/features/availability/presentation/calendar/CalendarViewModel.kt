package com.clockwise.features.availability.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.core.TimeProvider
import com.clockwise.core.util.formatTimeString
import com.clockwise.features.availability.data.repository.AvailabilityRepository
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.minus

class CalendarViewModel(
    private val repository: AvailabilityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        CalendarState(
        currentMonth = TimeProvider.getCurrentLocalDate()
    )
    )
    val state: StateFlow<CalendarState> = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: CalendarAction) {
        when (action) {
            is CalendarAction.LoadMonthlySchedule -> loadMonthlySchedule()
            is CalendarAction.SelectDate -> selectDate(action.date)
            is CalendarAction.SetAvailability -> setAvailability(action.date, action.startTime, action.endTime)
            is CalendarAction.EditAvailability -> editAvailability(action.date, action.startTime, action.endTime)
            is CalendarAction.DeleteAvailability -> deleteAvailability(action.date)
            is CalendarAction.ShowAvailabilityDialog -> showAvailabilityDialog()
            is CalendarAction.HideAvailabilityDialog -> hideAvailabilityDialog()
            is CalendarAction.ShowDeleteConfirmation -> showDeleteConfirmation(action.date)
            is CalendarAction.HideDeleteConfirmation -> hideDeleteConfirmation()
            is CalendarAction.NavigateToNextMonth -> navigateToNextMonth()
            is CalendarAction.NavigateToPreviousMonth -> navigateToPreviousMonth()
            is CalendarAction.NavigateToCurrentMonth -> navigateToCurrentMonth()
        }
    }
    
    private fun loadMonthlySchedule() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }
            
            repository.getUserAvailabilities().collect { result ->
                result.onSuccess { availabilities ->
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
                        val startTime = formatTimeString(startHour, startMinute)
                        val endTime = formatTimeString(endHour, endMinute)
                        
                        availabilityMap[date] = Pair(startTime, endTime)
                        
                        // Store the availability ID
                        availability.id?.let { id ->
                            availabilityIdMap[date] = id
                        }
                    }
                    
                    _state.update {
                        it.copy(
                            monthlySchedule = availabilityMap,
                            availabilityIdMap = availabilityIdMap,
                            isLoading = false
                        )
                    }
                }.onError { error ->
                    _state.update {
                        it.copy(
                            monthlySchedule = emptyMap(),
                            isLoading = false,
                            error = "Failed to load availabilities: ${error.name}"
                        )
                    }
                }
            }
        }
    }
    
    private fun selectDate(date: LocalDate) {
        _state.update {
            it.copy(
                selectedDate = date,
                showAvailabilityDialog = false
            )
        }
    }
    
    private fun setAvailability(date: LocalDate, startTime: String, endTime: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }
            
            // Check if we're updating an existing availability or creating a new one
            val existingAvailabilityId = _state.value.availabilityIdMap[date]
            
            val availabilityFlow = if (existingAvailabilityId != null) {
                // Update existing availability
                repository.updateAvailability(
                    id = existingAvailabilityId,
                    date = date,
                    startTimeString = startTime,
                    endTimeString = endTime
                )
            } else {
                // Create new availability
                repository.createAvailability(
                    date = date,
                    startTimeString = startTime,
                    endTimeString = endTime
                )
            }
            
            availabilityFlow.collect { result ->
                result.onSuccess { availabilityDto ->
                    // Update local state
                    _state.update { state ->
                        val updatedSchedule = state.monthlySchedule.toMutableMap()
                        updatedSchedule[date] = Pair(startTime, endTime)
                        
                        val updatedIdMap = state.availabilityIdMap.toMutableMap()
                        availabilityDto.id?.let { id ->
                            updatedIdMap[date] = id
                        }
                        
                        state.copy(
                            monthlySchedule = updatedSchedule,
                            availabilityIdMap = updatedIdMap,
                            isLoading = false,
                            showAvailabilityDialog = false
                        )
                    }
                }.onError { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            showAvailabilityDialog = false,
                            error = "Failed to save availability: ${error.name}"
                        )
                    }
                }
            }
        }
    }
    
    private fun editAvailability(date: LocalDate, startTime: String, endTime: String) {
        _state.update {
            it.copy(
                showAvailabilityDialog = true
            )
        }
    }
    
    private fun deleteAvailability(date: LocalDate) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }
            
            val availabilityId = _state.value.availabilityIdMap[date]
            
            if (availabilityId != null) {
                repository.deleteAvailability(availabilityId).collect { result ->
                    result.onSuccess { success ->
                        if (success) {
                            // Update local state
                            _state.update { state ->
                                val updatedSchedule = state.monthlySchedule.toMutableMap()
                                updatedSchedule.remove(date)
                                
                                val updatedIdMap = state.availabilityIdMap.toMutableMap()
                                updatedIdMap.remove(date)
                                
                                state.copy(
                                    monthlySchedule = updatedSchedule,
                                    availabilityIdMap = updatedIdMap,
                                    isLoading = false,
                                    showDeleteConfirmationDialog = false
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    showDeleteConfirmationDialog = false,
                                    error = "Failed to delete availability"
                                )
                            }
                        }
                    }.onError { error ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                showDeleteConfirmationDialog = false,
                                error = "Failed to delete availability: ${error.name}"
                            )
                        }
                    }
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "No availability ID found for the selected date"
                    )
                }
            }
        }
    }
    
    private fun showAvailabilityDialog() {
        _state.update {
            it.copy(
                showAvailabilityDialog = true
            )
        }
    }
    
    private fun hideAvailabilityDialog() {
        _state.update {
            it.copy(
                showAvailabilityDialog = false
            )
        }
    }
    
    private fun showDeleteConfirmation(date: LocalDate) {
        _state.update {
            it.copy(
                showDeleteConfirmationDialog = true
            )
        }
    }
    
    private fun hideDeleteConfirmation() {
        _state.update {
            it.copy(
                showDeleteConfirmationDialog = false
            )
        }
    }
    
    private fun navigateToNextMonth() {
        val currentDate = TimeProvider.getCurrentLocalDate()
        val targetDate = _state.value.currentMonth.plus(DatePeriod(months = 1))
        
        _state.update { currentState ->
            currentState.copy(
                currentMonth = targetDate
            )
        }
        
        // Reload availabilities for the new month
        loadMonthlySchedule()
    }
    
    private fun navigateToPreviousMonth() {
        val targetDate = _state.value.currentMonth.minus(DatePeriod(months = 1))
        
        _state.update { currentState ->
            currentState.copy(
                currentMonth = targetDate
            )
        }
        
        // Reload availabilities for the new month
        loadMonthlySchedule()
    }
    
    private fun navigateToCurrentMonth() {
        val currentDate = TimeProvider.getCurrentLocalDate()
        
        _state.update { currentState ->
            currentState.copy(
                currentMonth = currentDate
            )
        }
        
        // Reload availabilities for the current month
        loadMonthlySchedule()
    }
} 