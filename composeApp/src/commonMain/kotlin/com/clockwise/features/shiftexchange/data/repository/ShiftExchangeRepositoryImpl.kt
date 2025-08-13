package com.clockwise.features.shiftexchange.data.repository

import com.clockwise.features.shiftexchange.data.dto.CreateExchangeShiftRequest
import com.clockwise.features.shiftexchange.data.dto.CreateShiftRequestRequest
import com.clockwise.features.shiftexchange.data.dto.toDomain
import com.clockwise.features.shiftexchange.data.network.RemoteShiftExchangeDataSource
import com.clockwise.features.shiftexchange.domain.model.ExchangeShift
import com.clockwise.features.shiftexchange.domain.model.RequestType
import com.clockwise.features.shiftexchange.domain.model.ShiftRequest
import com.clockwise.features.shiftexchange.domain.repository.ShiftExchangeRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ShiftExchangeRepositoryImpl(
    private val remoteDataSource: RemoteShiftExchangeDataSource,
    private val userService: com.clockwise.features.auth.UserService
) : ShiftExchangeRepository {
    
    override suspend fun postShiftToMarketplace(
        planningServiceShiftId: String,
        businessUnitId: String,
        shiftPosition: String,
        shiftStartTime: String,
        shiftEndTime: String,
        userFirstName: String,
        userLastName: String
    ): Flow<Result<ExchangeShift, DataError.Remote>> {
        val currentUserId = userService.currentUser.value?.id
        if (currentUserId == null) {
            return flowOf(Result.Error(DataError.Remote.UNKNOWN))
        }
        
        val request = CreateExchangeShiftRequest(
            planningServiceShiftId = planningServiceShiftId,
            businessUnitId = businessUnitId,
            shiftPosition = shiftPosition,
            shiftStartTime = shiftStartTime,
            shiftEndTime = shiftEndTime,
            userId = currentUserId,
            userFirstName = userFirstName,
            userLastName = userLastName
        )
        
        return when (val result = remoteDataSource.postShiftToMarketplace(planningServiceShiftId, request)) {
            is Result.Success -> flowOf(Result.Success(result.data.toDomain()))
            is Result.Error -> flowOf(Result.Error(result.error))
        }
    }
    
    override suspend fun getAvailableShifts(
        businessUnitId: String,
        page: Int,
        size: Int
    ): Flow<Result<List<ExchangeShift>, DataError.Remote>> {
        return when (val result = remoteDataSource.getAvailableShifts(businessUnitId, page, size)) {
            is Result.Success -> {
                val exchangeShifts = result.data.exchangeShifts.map { it.toDomain() }
                flowOf(Result.Success(exchangeShifts))
            }
            is Result.Error -> flowOf(Result.Error(result.error))
        }
    }
    
    override suspend fun getMyPostedShifts(): Flow<Result<List<ExchangeShift>, DataError.Remote>> {
        return when (val result = remoteDataSource.getMyPostedShifts()) {
            is Result.Success -> {
                val exchangeShifts = result.data.map { it.toDomain() }
                flowOf(Result.Success(exchangeShifts))
            }
            is Result.Error -> flowOf(Result.Error(result.error))
        }
    }
    
    override suspend fun submitShiftRequest(
        exchangeShiftId: String,
        requestType: RequestType,
        swapShiftId: String?,
        swapShiftPosition: String?,
        swapShiftStartTime: String?,
        swapShiftEndTime: String?,
        requesterUserFirstName: String?,
        requesterUserLastName: String?
    ): Flow<Result<ShiftRequest, DataError.Remote>> {
        val currentUserId = userService.currentUser.value?.id
        if (currentUserId == null) {
            return flowOf(Result.Error(DataError.Remote.UNKNOWN))
        }
        
        println("DEBUG REPO: Creating request with:")
        println("DEBUG REPO: requestType = $requestType")
        println("DEBUG REPO: requesterUserId = $currentUserId")
        println("DEBUG REPO: swapShiftId = $swapShiftId")
        println("DEBUG REPO: swapShiftPosition = $swapShiftPosition")
        println("DEBUG REPO: swapShiftStartTime = '$swapShiftStartTime'")
        println("DEBUG REPO: swapShiftEndTime = '$swapShiftEndTime'")
        println("DEBUG REPO: requesterUserFirstName = $requesterUserFirstName")
        println("DEBUG REPO: requesterUserLastName = $requesterUserLastName")
        
        val request = CreateShiftRequestRequest(
            requestType = requestType,
            requesterUserId = currentUserId,
            swapShiftId = swapShiftId,
            swapShiftPosition = swapShiftPosition,
            swapShiftStartTime = swapShiftStartTime,
            swapShiftEndTime = swapShiftEndTime,
            requesterUserFirstName = requesterUserFirstName,
            requesterUserLastName = requesterUserLastName
        )
        
        return when (val result = remoteDataSource.submitShiftRequest(exchangeShiftId, request)) {
            is Result.Success -> flowOf(Result.Success(result.data.toDomain()))
            is Result.Error -> flowOf(Result.Error(result.error))
        }
    }
    
    override suspend fun getMyRequests(): Flow<Result<List<ShiftRequest>, DataError.Remote>> {
        return when (val result = remoteDataSource.getMyRequests()) {
            is Result.Success -> {
                val shiftRequests = result.data.map { it.toDomain() }
                flowOf(Result.Success(shiftRequests))
            }
            is Result.Error -> flowOf(Result.Error(result.error))
        }
    }
    
    override suspend fun getRequestsForMyShift(
        exchangeShiftId: String
    ): Flow<Result<List<ShiftRequest>, DataError.Remote>> {
        return when (val result = remoteDataSource.getRequestsForMyShift(exchangeShiftId)) {
            is Result.Success -> {
                val shiftRequests = result.data.map { it.toDomain() }
                flowOf(Result.Success(shiftRequests))
            }
            is Result.Error -> flowOf(Result.Error(result.error))
        }
    }
    
    override suspend fun acceptRequest(
        exchangeShiftId: String,
        requestId: String
    ): Flow<Result<ShiftRequest, DataError.Remote>> {
        return when (val result = remoteDataSource.acceptRequest(exchangeShiftId, requestId)) {
            is Result.Success -> flowOf(Result.Success(result.data.toDomain()))
            is Result.Error -> flowOf(Result.Error(result.error))
        }
    }
    
    override suspend fun cancelExchangeShift(
        exchangeShiftId: String
    ): Flow<Result<ExchangeShift, DataError.Remote>> {
        return when (val result = remoteDataSource.cancelExchangeShift(exchangeShiftId)) {
            is Result.Success -> flowOf(Result.Success(result.data.toDomain()))
            is Result.Error -> flowOf(Result.Error(result.error))
        }
    }
}