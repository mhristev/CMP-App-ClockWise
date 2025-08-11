package com.clockwise.features.shiftexchange.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.features.auth.UserService
import com.clockwise.features.shift.domain.repositories.ShiftRepository
import com.clockwise.features.shiftexchange.domain.usecase.AcceptRequestUseCase
import com.clockwise.features.shiftexchange.domain.usecase.GetAvailableShiftsUseCase
import com.clockwise.features.shiftexchange.domain.usecase.GetMyPostedShiftsUseCase
import com.clockwise.features.shiftexchange.domain.usecase.GetRequestsForMyShiftUseCase
import com.clockwise.features.shiftexchange.domain.usecase.PostShiftToMarketplaceUseCase
import com.clockwise.features.shiftexchange.domain.usecase.SubmitShiftRequestUseCase
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import com.clockwise.core.TimeProvider
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class ShiftExchangeViewModel(
    private val getAvailableShiftsUseCase: GetAvailableShiftsUseCase,
    private val getMyPostedShiftsUseCase: GetMyPostedShiftsUseCase,
    private val postShiftToMarketplaceUseCase: PostShiftToMarketplaceUseCase,
    private val submitShiftRequestUseCase: SubmitShiftRequestUseCase,
    private val getRequestsForMyShiftUseCase: GetRequestsForMyShiftUseCase,
    private val acceptRequestUseCase: AcceptRequestUseCase,
    private val shiftRepository: ShiftRepository,
    private val userService: UserService
) : ViewModel() {
    
    private val _state = MutableStateFlow(ShiftExchangeState())
    val state: StateFlow<ShiftExchangeState> = _state.asStateFlow()
    
    init {
        initializeUserContext()
        loadInitialData()
    }
    
    fun onAction(action: ShiftExchangeAction) {
        when (action) {
            is ShiftExchangeAction.SelectTab -> selectTab(action.tab)
            is ShiftExchangeAction.LoadAvailableShifts -> loadAvailableShifts()
            is ShiftExchangeAction.LoadMyPostedShifts -> loadMyPostedShifts()
            is ShiftExchangeAction.LoadUserShifts -> loadUserShifts()
            is ShiftExchangeAction.LoadRequestsForShift -> loadRequestsForShift(action.exchangeShiftId)
            
            is ShiftExchangeAction.ShowPostShiftDialog -> showPostShiftDialog()
            is ShiftExchangeAction.HidePostShiftDialog -> hidePostShiftDialog()
            is ShiftExchangeAction.SelectShiftToPost -> selectShiftToPost(action.shift)
            is ShiftExchangeAction.PostShiftToMarketplace -> postShiftToMarketplace(action.shift)
            
            is ShiftExchangeAction.ShowRequestDialog -> showRequestDialog(action.exchangeShift)
            is ShiftExchangeAction.HideRequestDialog -> hideRequestDialog()
            is ShiftExchangeAction.SubmitShiftRequest -> submitShiftRequest(
                action.exchangeShiftId, 
                action.requestType, 
                action.swapShiftId,
                action.swapShiftPosition,
                action.swapShiftStartTime,
                action.swapShiftEndTime,
                action.requesterUserFirstName,
                action.requesterUserLastName
            )
            
            is ShiftExchangeAction.ShowRequestsDialog -> showRequestsDialog(action.exchangeShift)
            is ShiftExchangeAction.HideRequestsDialog -> hideRequestsDialog()
            is ShiftExchangeAction.AcceptRequest -> acceptRequest(action.exchangeShiftId, action.requestId)
            is ShiftExchangeAction.CancelExchangeShift -> cancelExchangeShift(action.exchangeShiftId)
            
            is ShiftExchangeAction.ClearError -> clearError()
        }
    }
    
    private fun initializeUserContext() {
        println("DEBUG: ShiftExchangeViewModel - initializeUserContext called")
        viewModelScope.launch {
            userService.currentUser.collect { user ->
                println("DEBUG: ShiftExchangeViewModel - User updated: ${user?.email}, BusinessUnitId: ${user?.businessUnitId}")
                _state.update { 
                    it.copy(currentBusinessUnitId = user?.businessUnitId)
                }
            }
        }
    }
    
    private fun loadInitialData() {
        println("DEBUG: ShiftExchangeViewModel - loadInitialData called")
        loadAvailableShifts()
        loadMyPostedShifts()
        loadUserShifts()
    }
    
    private fun selectTab(tab: ShiftExchangeTab) {
        println("DEBUG: ShiftExchangeViewModel - selectTab called with: $tab")
        _state.update { it.copy(selectedTab = tab) }
        when (tab) {
            ShiftExchangeTab.AVAILABLE_SHIFTS -> {
                println("DEBUG: ShiftExchangeViewModel - Switching to AVAILABLE_SHIFTS tab, calling loadAvailableShifts()")
                loadAvailableShifts()
            }
            ShiftExchangeTab.MY_POSTED_EXCHANGES -> {
                println("DEBUG: ShiftExchangeViewModel - Switching to MY_POSTED_EXCHANGES tab, calling loadMyPostedShifts()")
                loadMyPostedShifts()
            }
        }
    }
    
    private fun loadAvailableShifts() {
        val businessUnitId = _state.value.currentBusinessUnitId ?: run {
            println("DEBUG: loadAvailableShifts - No businessUnitId available")
            return
        }
        
        println("DEBUG: loadAvailableShifts - Starting load for businessUnitId: $businessUnitId")
        
        viewModelScope.launch {
            _state.update { it.copy(isLoadingAvailableShifts = true) }
            
            println("DEBUG: loadAvailableShifts - About to call getAvailableShiftsUseCase")
            println("DEBUG: loadAvailableShiftsUseCase class: ${getAvailableShiftsUseCase::class.simpleName}")
            
            getAvailableShiftsUseCase(businessUnitId).collect { result ->
                println("DEBUG: loadAvailableShifts - Got result type: ${result::class.simpleName}")
                _state.update {
                    when (result) {
                        is Result.Success -> {
                            println("DEBUG: loadAvailableShifts - Success with ${result.data.size} posted shifts from marketplace")
                            result.data.forEach { exchangeShift ->
                                println("DEBUG: loadAvailableShifts - ExchangeShift: ${exchangeShift.id}, ${exchangeShift.position}, ${exchangeShift.posterName}")
                            }
                            it.copy(
                                availableShifts = result.data,
                                isLoadingAvailableShifts = false,
                                errorMessage = null
                            )
                        }
                        is Result.Error -> {
                            println("DEBUG: loadAvailableShifts - Error: ${result.error}")
                            it.copy(
                                isLoadingAvailableShifts = false,
                                errorMessage = "Failed to load available shifts"
                            )
                        }
                    }
                }
            }
        }
    }
    
    private fun loadMyPostedShifts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMyPostedShifts = true) }
            
            getMyPostedShiftsUseCase().collect { result ->
                _state.update { currentState ->
                    when (result) {
                        is Result.Success -> {
                            // Load requests for each posted shift
                            result.data.forEach { exchangeShift ->
                                loadRequestsForShift(exchangeShift.id)
                            }
                            
                            currentState.copy(
                                myPostedShifts = result.data,
                                isLoadingMyPostedShifts = false,
                                errorMessage = null
                            )
                        }
                        is Result.Error -> currentState.copy(
                            isLoadingMyPostedShifts = false,
                            errorMessage = "Failed to load posted shifts"
                        )
                    }
                }
            }
        }
    }
    
    private fun loadUserShifts() {
        println("DEBUG: loadUserShifts - Loading user's upcoming shifts for PostShiftDialog")
        viewModelScope.launch {
            _state.update { it.copy(isLoadingUserShifts = true) }
            
            // Use the same getUpcomingShifts API that works in clock-in view
            shiftRepository.getUpcomingShifts().collect { result ->
                _state.update {
                    when (result) {
                        is Result.Success -> {
                            println("DEBUG: loadUserShifts - Converting ${result.data.size} ShiftDtos to Shift domain models")
                            // Convert DTOs to domain models (same logic as GetAvailableShiftsUseCase)
                            val shifts = result.data.map { shiftDto ->
                                val startTime = TimeProvider.epochSecondsToLocalDateTime(shiftDto.startTime)
                                val endTime = TimeProvider.epochSecondsToLocalDateTime(shiftDto.endTime)

                                val workSession = shiftDto.workSession?.let { wsDto ->
                                    val sessionNote = wsDto.sessionNote?.let { noteDto ->
                                        com.clockwise.features.shift.domain.model.SessionNote(
                                            id = noteDto.id,
                                            workSessionId = noteDto.workSessionId,
                                            content = noteDto.content,
                                            createdAt = TimeProvider.epochSecondsToLocalDateTime(noteDto.createdAt)
                                        )
                                    }
                                    
                                    com.clockwise.features.shift.domain.model.WorkSession(
                                        id = wsDto.id,
                                        userId = wsDto.userId,
                                        shiftId = wsDto.shiftId,
                                        clockInTime = wsDto.clockInTime?.let { TimeProvider.epochSecondsToLocalDateTime(it) },
                                        clockOutTime = wsDto.clockOutTime?.let { TimeProvider.epochSecondsToLocalDateTime(it) },
                                        totalMinutes = wsDto.totalMinutes,
                                        status = com.clockwise.features.shift.domain.model.WorkSessionStatus.fromString(wsDto.status),
                                        sessionNote = sessionNote
                                    )
                                }
                                
                                com.clockwise.features.shift.domain.model.Shift(
                                    id = shiftDto.id,
                                    startTime = startTime,
                                    endTime = endTime,
                                    position = shiftDto.position ?: "General Staff",
                                    employeeId = shiftDto.employeeId,
                                    workSession = workSession,
                                    status = workSession?.let {
                                        when(it.status) {
                                            com.clockwise.features.shift.domain.model.WorkSessionStatus.CREATED -> com.clockwise.features.shift.domain.model.ShiftStatus.SCHEDULED
                                            com.clockwise.features.shift.domain.model.WorkSessionStatus.ACTIVE -> com.clockwise.features.shift.domain.model.ShiftStatus.CLOCKED_IN
                                            com.clockwise.features.shift.domain.model.WorkSessionStatus.COMPLETED -> com.clockwise.features.shift.domain.model.ShiftStatus.COMPLETED
                                            else -> com.clockwise.features.shift.domain.model.ShiftStatus.SCHEDULED
                                        }
                                    } ?: com.clockwise.features.shift.domain.model.ShiftStatus.SCHEDULED,
                                    clockInTime = workSession?.clockInTime,
                                    clockOutTime = workSession?.clockOutTime
                                )
                            }
                            println("DEBUG: loadUserShifts - Loaded ${shifts.size} user shifts for PostShiftDialog")
                            shifts.forEach { shift ->
                                println("DEBUG: loadUserShifts - Shift: ${shift.id}, ${shift.position}, ${shift.startTime}")
                            }
                            it.copy(
                                userShifts = shifts,
                                isLoadingUserShifts = false,
                                errorMessage = null
                            )
                        }
                        is Result.Error -> {
                            println("DEBUG: loadUserShifts - Error loading user shifts: ${result.error}")
                            it.copy(
                                isLoadingUserShifts = false,
                                errorMessage = "Failed to load your shifts"
                            )
                        }
                    }
                }
            }
        }
    }
    
    private fun loadRequestsForShift(exchangeShiftId: String) {
        viewModelScope.launch {
            getRequestsForMyShiftUseCase(exchangeShiftId).collect { result ->
                _state.update { currentState ->
                    when (result) {
                        is Result.Success -> {
                            val updatedRequests = currentState.myShiftRequests.toMutableMap()
                            updatedRequests[exchangeShiftId] = result.data
                            currentState.copy(myShiftRequests = updatedRequests)
                        }
                        is Result.Error -> currentState.copy(
                            errorMessage = "Failed to load requests for shift"
                        )
                    }
                }
            }
        }
    }
    
    private fun showPostShiftDialog() {
        println("DEBUG: showPostShiftDialog - Opening PostShiftDialog")
        println("DEBUG: showPostShiftDialog - Current userShifts count: ${_state.value.userShifts.size}")
        _state.update { it.copy(showPostShiftDialog = true) }
        // Make sure we have fresh user shifts data
        if (_state.value.userShifts.isEmpty()) {
            println("DEBUG: showPostShiftDialog - No user shifts loaded, calling loadUserShifts()")
            loadUserShifts()
        }
    }
    
    private fun hidePostShiftDialog() {
        _state.update { 
            it.copy(
                showPostShiftDialog = false,
                selectedShiftToPost = null
            ) 
        }
    }
    
    private fun selectShiftToPost(shift: com.clockwise.features.shift.domain.model.Shift) {
        _state.update { it.copy(selectedShiftToPost = shift) }
    }
    
    private fun postShiftToMarketplace(shift: com.clockwise.features.shift.domain.model.Shift) {
        val businessUnitId = _state.value.currentBusinessUnitId ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Get user information
            val currentUser = userService.currentUser.value
            if (currentUser == null) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "User information not available"
                    )
                }
                return@launch
            }
            
            // Format times as ISO 8601 strings
            val shiftStartTime = "${shift.startTime}:00Z"
            val shiftEndTime = "${shift.endTime}:00Z"
            
            postShiftToMarketplaceUseCase(
                planningServiceShiftId = shift.id,
                businessUnitId = businessUnitId,
                shiftPosition = shift.position,
                shiftStartTime = shiftStartTime,
                shiftEndTime = shiftEndTime,
                userFirstName = currentUser.firstName,
                userLastName = currentUser.lastName
            ).collect { result ->
                _state.update { currentState ->
                    when (result) {
                        is Result.Success -> {
                            hidePostShiftDialog()
                            loadMyPostedShifts() // Refresh the posted shifts
                            currentState.copy(
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        is Result.Error -> currentState.copy(
                            isLoading = false,
                            errorMessage = "Failed to post shift to marketplace"
                        )
                    }
                }
            }
        }
    }
    
    private fun showRequestDialog(exchangeShift: com.clockwise.features.shiftexchange.domain.model.ExchangeShift) {
        println("DEBUG: showRequestDialog - Opening request dialog for exchange shift: ${exchangeShift.id}")
        _state.update { 
            it.copy(
                showRequestDialog = true,
                selectedExchangeShift = exchangeShift
            ) 
        }
    }
    
    private fun hideRequestDialog() {
        _state.update { 
            it.copy(
                showRequestDialog = false,
                selectedExchangeShift = null
            ) 
        }
    }
    
    private fun submitShiftRequest(
        exchangeShiftId: String,
        requestType: com.clockwise.features.shiftexchange.domain.model.RequestType,
        swapShiftId: String?,
        swapShiftPosition: String?,
        swapShiftStartTime: String?,
        swapShiftEndTime: String?,
        requesterUserFirstName: String?,
        requesterUserLastName: String?
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Get current user information
            val currentUser = userService.currentUser.value
            val userFirstName = requesterUserFirstName ?: currentUser?.firstName
            val userLastName = requesterUserLastName ?: currentUser?.lastName
            
            if (currentUser == null) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "User information not available"
                    )
                }
                return@launch
            }
            
            submitShiftRequestUseCase(
                exchangeShiftId = exchangeShiftId,
                requestType = requestType,
                swapShiftId = swapShiftId,
                swapShiftPosition = swapShiftPosition,
                swapShiftStartTime = swapShiftStartTime,
                swapShiftEndTime = swapShiftEndTime,
                requesterUserFirstName = userFirstName,
                requesterUserLastName = userLastName
            ).collect { result ->
                _state.update { currentState ->
                    when (result) {
                        is Result.Success -> {
                            hideRequestDialog()
                            loadAvailableShifts() // Refresh available shifts
                            currentState.copy(
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        is Result.Error -> currentState.copy(
                            isLoading = false,
                            errorMessage = "Failed to submit shift request"
                        )
                    }
                }
            }
        }
    }
    
    private fun showRequestsDialog(exchangeShift: com.clockwise.features.shiftexchange.domain.model.ExchangeShift) {
        _state.update { 
            it.copy(
                showRequestsDialog = true,
                selectedExchangeShiftForRequests = exchangeShift
            ) 
        }
        loadRequestsForShift(exchangeShift.id)
    }
    
    private fun hideRequestsDialog() {
        _state.update { 
            it.copy(
                showRequestsDialog = false,
                selectedExchangeShiftForRequests = null
            ) 
        }
    }
    
    private fun acceptRequest(exchangeShiftId: String, requestId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            acceptRequestUseCase(exchangeShiftId, requestId).collect { result ->
                _state.update { currentState ->
                    when (result) {
                        is Result.Success -> {
                            hideRequestsDialog()
                            loadMyPostedShifts() // Refresh posted shifts
                            currentState.copy(
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        is Result.Error -> currentState.copy(
                            isLoading = false,
                            errorMessage = "Failed to accept request"
                        )
                    }
                }
            }
        }
    }
    
    private fun cancelExchangeShift(exchangeShiftId: String) {
        // This would be implemented similarly to accept request
        // For now, just refresh the data
        loadMyPostedShifts()
    }
    
    private fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}