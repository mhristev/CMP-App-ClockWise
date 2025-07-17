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
import com.clockwise.features.clockin.domain.service.LocationService
import com.clockwise.features.clockin.domain.service.LocationResult
import com.clockwise.features.organization.domain.repository.OrganizationRepository
import com.clockwise.features.auth.UserService
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.domain.DataError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.math.*

class WelcomeViewModel(
    private val shiftRepository: ShiftRepository,
    private val locationService: LocationService,
    private val organizationRepository: OrganizationRepository,
    private val userService: UserService
) : ViewModel() {

    init {
        println("🔍🔍🔍 WelcomeViewModel initialized with LocationService: ${locationService::class.simpleName} 🔍🔍🔍")
        println("🔍🔍🔍 LocationService implementation active 🔍🔍🔍")
    }

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
            is WelcomeAction.RefreshShifts -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isRefreshing = true,
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

                            println("Refreshed - Today: $today, Today's shift: ${todayShift?.startTime?.date}, Upcoming shifts: ${upcomingShifts.size}")
                            
                            // Update state with the shifts
                            _state.update {
                                it.copy(
                                    todayShift = todayShift,
                                    upcomingShifts = upcomingShifts,
                                    isRefreshing = false,
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
                                    isRefreshing = false,
                                    error = getErrorMessage(error, "refresh shifts")
                                )
                            }
                        }
                    }
                }
            }
            is WelcomeAction.ClockIn -> {
                viewModelScope.launch {
                    performLocationBasedClockIn(action.shiftId)
                }
            }
            is WelcomeAction.ClockOut -> {
                viewModelScope.launch {
                    performLocationBasedClockOut(action.shiftId)
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
                    performLocationBasedClockOut(action.shiftId, action.note, action.workSessionId)
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
            is WelcomeAction.RequestLocationPermission -> {
                // Permission request is handled by the UI layer (Android/iOS specific)
                // This action just dismisses the dialog - the actual permission handling 
                // is done in the platform-specific WelcomeScreen implementations
                _state.update { 
                    it.copy(
                        showLocationPermissionDialog = false
                    ) 
                }
            }
            is WelcomeAction.DismissLocationPermissionDialog -> {
                _state.update { 
                    it.copy(
                        showLocationPermissionDialog = false,
                        pendingClockInShiftId = null
                    ) 
                }
            }
            is WelcomeAction.DismissLocationRequiredDialog -> {
                _state.update { 
                    it.copy(
                        showLocationRequiredDialog = false,
                        pendingClockInShiftId = null
                    ) 
                }
            }
            is WelcomeAction.DismissLocationOutOfRangeDialog -> {
                _state.update { 
                    it.copy(
                        showLocationOutOfRangeDialog = false,
                        distanceFromWorkplace = null,
                        pendingClockInShiftId = null,
                        userLocation = null,
                        userAddress = null
                    ) 
                }
            }
            is WelcomeAction.RetryLocationCheck -> {
                val shiftId = _state.value.pendingClockInShiftId
                if (shiftId != null) {
                    viewModelScope.launch {
                        performLocationBasedClockIn(shiftId)
                    }
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

    private suspend fun performLocationBasedClockOut(shiftId: String, note: String? = null, workSessionId: String? = null) {
        _state.update { it.copy(isCheckingLocation = true) }
        
        try {
            println("DEBUG: Starting location-based clock-out for shift $shiftId")
            
            // Get business unit location
            val businessUnitLocation = getBusinessUnitLocation()
            if (businessUnitLocation == null) {
                println("DEBUG: Failed to get business unit location for clock-out")
                _state.update { 
                    it.copy(
                        isCheckingLocation = false,
                        error = "Unable to retrieve workplace location. Please try again."
                    ) 
                }
                return
            }
            
            println("DEBUG: Business unit location retrieved for clock-out: ${businessUnitLocation.first}, ${businessUnitLocation.second}")
            
            // Get ultra-fresh current location using enhanced method
            val currentLocation = requestUltraFreshLocation("CLOCK-OUT")
            if (currentLocation == null) {
                println("DEBUG: Failed to get ultra-fresh location for clock-out")
                return
            }
            
            // Calculate distance between fresh user location and workplace
            val distance = calculateDistance(
                currentLocation.first,
                currentLocation.second,
                businessUnitLocation.first,
                businessUnitLocation.second
            )
            
            // Debug logging with fresh location verification
            println("DEBUG: ========== FRESH LOCATION CLOCK-OUT ==========")
            println("DEBUG: Fresh user location: ${currentLocation.first}, ${currentLocation.second}")
            println("DEBUG: Business unit location: ${businessUnitLocation.first}, ${businessUnitLocation.second}")
            println("DEBUG: Calculated distance: $distance meters")
            println("DEBUG: Distance threshold: 200.0 meters")
            println("DEBUG: Within range: ${distance <= 200.0}")
            println("DEBUG: =======================================")
            
            // Check if within 200 meter radius
            if (distance <= 200.0) {
                println("DEBUG: User is within range - proceeding with clock-out")
                // Location is valid, proceed with clock out
                _state.update { 
                    it.copy(
                        isCheckingLocation = false,
                        isLoading = true
                    ) 
                }
                
                // Save note first if provided and workSessionId exists
                if (!note.isNullOrBlank() && workSessionId != null) {
                    shiftRepository.saveSessionNote(workSessionId, note).collect { noteResult ->
                        noteResult.onError { error ->
                            println("Failed to save session note: ${getErrorMessage(error, "save note")}")
                            // Continue with clock out even if note save fails
                        }
                    }
                }
                
                // Then perform clock-out
                shiftRepository.clockOut(shiftId).collect { result ->
                    result.onSuccess {
                        // Clear session notes for this shift's work session on successful clock out
                        val currentState = _state.value
                        val shiftToClockOut = currentState.todayShift?.takeIf { it.id == shiftId }
                        val workSessionIdToRemove = workSessionId ?: shiftToClockOut?.workSession?.id
                        
                        val updatedSessionNotes = if (workSessionIdToRemove != null) {
                            currentState.sessionNotes.filterKeys { it != workSessionIdToRemove }
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
                                error = getErrorMessage(error, "clock out")
                            )
                        }
                    }
                }
            } else {
                println("DEBUG: User is TOO FAR from workplace for clock-out - showing location dialog")
                // Location is too far, get user address and show error dialog
                val userAddress = getAddressFromCoordinates(currentLocation.first, currentLocation.second)
                println("DEBUG: User address for clock-out: $userAddress")
                
                _state.update { 
                    it.copy(
                        isCheckingLocation = false,
                        showLocationOutOfRangeDialog = true,
                        distanceFromWorkplace = distance,
                        userLocation = currentLocation,
                        userAddress = userAddress
                    ) 
                }
            }
        } catch (e: Exception) {
            _state.update { 
                it.copy(
                    isCheckingLocation = false,
                    error = "Failed to check location for clock-out: ${e.message}"
                ) 
            }
        }
    }

    private suspend fun performLocationBasedClockIn(shiftId: String) {
        _state.update { it.copy(isCheckingLocation = true) }
        
        try {
            println("DEBUG: Starting location-based clock-in for shift $shiftId")
            
            // Get business unit location
            val businessUnitLocation = getBusinessUnitLocation()
            if (businessUnitLocation == null) {
                println("DEBUG: Failed to get business unit location")
                _state.update { 
                    it.copy(
                        isCheckingLocation = false,
                        error = "Unable to retrieve workplace location. Please try again."
                    ) 
                }
                return
            }
            
            println("DEBUG: Business unit location retrieved: ${businessUnitLocation.first}, ${businessUnitLocation.second}")
            
            // Get ultra-fresh current location using enhanced method
            val currentLocation = requestUltraFreshLocation("CLOCK-IN")
            if (currentLocation == null) {
                println("DEBUG: Failed to get ultra-fresh location for clock-in")
                return
            }
            
            // Calculate distance between fresh user location and workplace
            val distance = calculateDistance(
                currentLocation.first,
                currentLocation.second,
                businessUnitLocation.first,
                businessUnitLocation.second
            )
            
            // Debug logging with fresh location verification
            println("DEBUG: ========== FRESH LOCATION CLOCK-IN ==========")
            println("DEBUG: Fresh user location: ${currentLocation.first}, ${currentLocation.second}")
            println("DEBUG: Business unit location: ${businessUnitLocation.first}, ${businessUnitLocation.second}")
            println("DEBUG: Calculated distance: $distance meters")
            println("DEBUG: Distance threshold: 200.0 meters")
            println("DEBUG: Within range: ${distance <= 200.0}")
            println("DEBUG: =======================================")
            
            // Check if within 200 meter radius
            if (distance <= 200.0) {
                println("DEBUG: User is within range - proceeding with clock-in")
                // Location is valid, proceed with clock in
                _state.update { 
                    it.copy(
                        isCheckingLocation = false,
                        pendingClockInShiftId = null,
                        isLoading = true
                    ) 
                }
                
                shiftRepository.clockIn(shiftId).collect { result ->
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
            } else {
                println("DEBUG: User is TOO FAR from workplace - showing location dialog")
                // Location is too far, get user address and show error dialog
                val userAddress = getAddressFromCoordinates(currentLocation.first, currentLocation.second)
                println("DEBUG: User address: $userAddress")
                
                _state.update { 
                    it.copy(
                        isCheckingLocation = false,
                        showLocationOutOfRangeDialog = true,
                        distanceFromWorkplace = distance,
                        userLocation = currentLocation,
                        userAddress = userAddress
                    ) 
                }
            }
        } catch (e: Exception) {
            _state.update { 
                it.copy(
                    isCheckingLocation = false,
                    error = "Failed to check location: ${e.message}"
                ) 
            }
        }
    }

    private suspend fun getBusinessUnitLocation(): Pair<Double, Double>? {
        return try {
            // Get the business unit ID from the current user
            val currentUser = userService.currentUser.value
            val businessUnitId = currentUser?.businessUnitId
            
            if (businessUnitId == null) {
                println("DEBUG: No business unit ID found for current user")
                return _state.value.businessUnitLocation
            }
            
            organizationRepository.getBusinessUnitById(businessUnitId)
                .onSuccess { businessUnitAddress ->
                    println("DEBUG: Successfully fetched business unit location from API: ${businessUnitAddress.name} at ${businessUnitAddress.latitude}, ${businessUnitAddress.longitude}")
                    
                    // Cache the address in state for display
                    _state.update { currentState ->
                        currentState.copy(
                            businessUnitAddress = businessUnitAddress.address
                        )
                    }
                    
                    return Pair(businessUnitAddress.latitude, businessUnitAddress.longitude)
                }
                .onError { error ->
                    println("DEBUG: Failed to fetch business unit location from API: $error")
                    // Fallback to cached location if available
                    return _state.value.businessUnitLocation
                }
            
            // Should not reach here due to explicit returns above
            null
        } catch (e: Exception) {
            println("DEBUG: Exception while fetching business unit location: ${e.message}")
            // Fallback to cached location if available
            _state.value.businessUnitLocation
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0 // Earth radius in meters
        
        val dLat = (lat2 - lat1) * kotlin.math.PI / 180
        val dLon = (lon2 - lon1) * kotlin.math.PI / 180
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1 * kotlin.math.PI / 180) * cos(lat2 * kotlin.math.PI / 180) *
                sin(dLon / 2) * sin(dLon / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return earthRadius * c
    }

    private suspend fun getAddressFromCoordinates(latitude: Double, longitude: Double): String {
        // For now, return a mock address. In a real implementation, you would:
        // 1. Use a geocoding service like Google Maps API
        // 2. Or implement a reverse geocoding function
        return "Address: ${latitude.toString().take(7)}, ${longitude.toString().take(7)}"
    }

    /**
     * Helper method to request zero-tolerance fresh location with enhanced debugging and timing
     */
    private suspend fun requestUltraFreshLocation(operation: String): Pair<Double, Double>? {
        val requestTime = Clock.System.now()
        println("DEBUG: 🚨🚨🚨 ZERO-TOLERANCE LOCATION REQUEST 🚨🚨🚨")
        println("DEBUG: Operation: $operation")
        println("DEBUG: Request timestamp: $requestTime")
        println("DEBUG: 🚨🚨🚨 DEMANDING ZERO-TOLERANCE FRESH LOCATION - ABSOLUTELY NO CACHE! 🚨🚨🚨")
        println("DEBUG: 🚨 User clicked: $operation - MUST get fresh GPS coordinates!")
        println("DEBUG: 🚨🚨🚨 ================================================================ 🚨🚨🚨")
        
        // Update UI to show we're getting fresh location
        _state.update { 
            it.copy(
                isCheckingLocation = true,
                error = null
            ) 
        }
        
        val locationResult = locationService.getCurrentLocation()
        val responseTime = Clock.System.now()
        val totalRequestTime = (responseTime - requestTime).inWholeMilliseconds
        
        println("DEBUG: 🚨🚨🚨 ZERO-TOLERANCE LOCATION RESPONSE 🚨🚨🚨")
        println("DEBUG: Operation: $operation")
        println("DEBUG: Response timestamp: $responseTime")
        println("DEBUG: Total request time: ${totalRequestTime}ms")
        println("DEBUG: 🚨🚨🚨 ================================================================ 🚨🚨🚨")
        
        return when (locationResult) {
            is LocationResult.Success -> {
                println("DEBUG: 🚨🚨🚨 ✅ SUCCESS! Got ZERO-TOLERANCE fresh location for $operation 🚨🚨🚨")
                println("DEBUG: 🚨 FRESH COORDINATES: ${locationResult.latitude}, ${locationResult.longitude}")
                println("DEBUG: 🚨 Total time from user click to fresh GPS: ${totalRequestTime}ms")
                println("DEBUG: 🚨🚨🚨 NO CACHED DATA WAS USED! 🚨🚨🚨")
                Pair(locationResult.latitude, locationResult.longitude)
            }
            is LocationResult.PermissionDenied -> {
                println("DEBUG: 🚨🚨🚨 ❌ Location permission denied for $operation 🚨🚨🚨")
                _state.update { 
                    it.copy(
                        isCheckingLocation = false,
                        showLocationRequiredDialog = true
                    ) 
                }
                null
            }
            is LocationResult.LocationUnavailable -> {
                println("DEBUG: ❌ Location services unavailable for $operation")
                _state.update { 
                    it.copy(
                        isCheckingLocation = false,
                        error = "Location services are unavailable. Please check your device settings."
                    ) 
                }
                null
            }
            is LocationResult.LocationDisabled -> {
                println("DEBUG: ❌ Location services disabled for $operation")
                _state.update { 
                    it.copy(
                        isCheckingLocation = false,
                        error = "Location services are disabled. Please enable GPS to continue."
                    ) 
                }
                null
            }
            is LocationResult.Error -> {
                println("DEBUG: ❌ Location error for $operation: ${locationResult.message}")
                _state.update { 
                    it.copy(
                        isCheckingLocation = false,
                        error = "Failed to get fresh location: ${locationResult.message}"
                    ) 
                }
                null
            }
        }
    }
}