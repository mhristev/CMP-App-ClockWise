package com.clockwise.features.availability.calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.core.TimeProvider
import com.clockwise.core.util.formatTimeString
import com.clockwise.features.availability.calendar.domain.AvailabilityRepository
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
    private val availabilityRepository: AvailabilityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CalendarState(
        currentMonth = TimeProvider.getCurrentLocalDate()
    ))
    val state: StateFlow<CalendarState> = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: CalendarAction) {
        when (action) {
            is CalendarAction.LoadMonthlySchedule -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                    
                    try {
                        // Load availabilities from the API
                        val availabilities = availabilityRepository.getUserAvailabilities()
                        
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
                    } catch (e: Exception) {
                        println("Error loading availabilities: ${e.message}")
                        e.printStackTrace()
                        
                        _state.update {
                            it.copy(
                                monthlySchedule = emptyMap(),
                                isLoading = false
                            )
                        }
                    }
                }
            }
            is CalendarAction.SelectDate -> {
                _state.update {
                    it.copy(
                        selectedDate = action.date,
                        showAvailabilityDialog = false
                    )
                }
            }
            is CalendarAction.SetAvailability -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                    
                    try {
                        // Check if we're updating an existing availability or creating a new one
                        val existingAvailabilityId = _state.value.availabilityIdMap[action.date]
                        val availabilityDto = if (existingAvailabilityId != null) {
                            // Update existing availability
                            println("Updating existing availability with ID: $existingAvailabilityId")
                            availabilityRepository.updateAvailability(
                                id = existingAvailabilityId,
                                date = action.date,
                                startTimeString = action.startTime,
                                endTimeString = action.endTime
                            )
                        } else {
                            // Create new availability
                            println("Creating new availability")
                            availabilityRepository.createAvailability(
                                date = action.date,
                                startTimeString = action.startTime,
                                endTimeString = action.endTime
                            )
                        }
                        
                        // Update local state
                        _state.update { state ->
                            val updatedSchedule = state.monthlySchedule.toMutableMap()
                            updatedSchedule[action.date] = Pair(action.startTime, action.endTime)
                            
                            val updatedIdMap = state.availabilityIdMap.toMutableMap()
                            availabilityDto.id?.let { id ->
                                updatedIdMap[action.date] = id
                            }
                            
                            state.copy(
                                monthlySchedule = updatedSchedule,
                                availabilityIdMap = updatedIdMap,
                                isLoading = false,
                                showAvailabilityDialog = false
                            )
                        }
                    } catch (e: Exception) {
                        println("Error submitting availability: ${e.message}")
                        e.printStackTrace()
                        
                        // Update state with error
                        _state.update {
                            it.copy(
                                isLoading = false,
                                showAvailabilityDialog = false
                            )
                        }
                    }
                }
            }
            is CalendarAction.EditAvailability -> {
                _state.update {
                    it.copy(
                        showAvailabilityDialog = true
                    )
                }
            }
            is CalendarAction.DeleteAvailability -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                    
                    try {
                        val availabilityId = _state.value.availabilityIdMap[action.date]
                        
                        if (availabilityId != null) {
                            // Delete availability from the backend
                            val success = availabilityRepository.deleteAvailability(availabilityId)
                            
                            if (success) {
                                // Update local state
                                _state.update { state ->
                                    val updatedSchedule = state.monthlySchedule.toMutableMap()
                                    updatedSchedule.remove(action.date)
                                    
                                    val updatedIdMap = state.availabilityIdMap.toMutableMap()
                                    updatedIdMap.remove(action.date)
                                    
                                    state.copy(
                                        monthlySchedule = updatedSchedule,
                                        availabilityIdMap = updatedIdMap,
                                        isLoading = false
                                    )
                                }
                            } else {
                                println("Error deleting availability: Backend reported failure")
                                _state.update {
                                    it.copy(
                                        isLoading = false
                                    )
                                }
                            }
                        } else {
                            println("Error deleting availability: No ID found for date")
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                        }
                    } catch (e: Exception) {
                        println("Error deleting availability: ${e.message}")
                        e.printStackTrace()
                        
                        _state.update {
                            it.copy(
                                isLoading = false
                            )
                        }
                    }
                }
            }
            is CalendarAction.ShowAvailabilityDialog -> {
                _state.update {
                    it.copy(
                        showAvailabilityDialog = true
                    )
                }
            }
            is CalendarAction.HideAvailabilityDialog -> {
                _state.update {
                    it.copy(
                        showAvailabilityDialog = false
                    )
                }
            }
            is CalendarAction.ShowDeleteConfirmation -> {
                _state.update {
                    it.copy(
                        showDeleteConfirmationDialog = true
                    )
                }
            }
            is CalendarAction.HideDeleteConfirmation -> {
                _state.update {
                    it.copy(
                        showDeleteConfirmationDialog = false
                    )
                }
            }
            is CalendarAction.NavigateToNextMonth -> {
                val currentDate = TimeProvider.getCurrentLocalDate()
                val targetDate = _state.value.currentMonth.plus(DatePeriod(months = 1))
                val maxDate = currentDate.plus(DatePeriod(months = 3))
                
                // Compare years and months separately to handle year transitions
                val isWithinRange = (targetDate.year < maxDate.year) || 
                    (targetDate.year == maxDate.year && targetDate.month.ordinal <= maxDate.month.ordinal)
                
                if (isWithinRange) {
                    _state.update { state ->
                        state.copy(
                            currentMonth = targetDate
                        )
                    }
                }
            }
            is CalendarAction.NavigateToPreviousMonth -> {
                val currentDate = TimeProvider.getCurrentLocalDate()
                val targetDate = _state.value.currentMonth.minus(DatePeriod(months = 1))
                val minDate = currentDate.minus(DatePeriod(months = 3))
                
                // Compare years and months separately to handle year transitions
                val isWithinRange = (targetDate.year > minDate.year) || 
                    (targetDate.year == minDate.year && targetDate.month.ordinal >= minDate.month.ordinal)
                
                if (isWithinRange) {
                    _state.update { state ->
                        state.copy(
                            currentMonth = targetDate
                        )
                    }
                }
            }
        }
    }
} 