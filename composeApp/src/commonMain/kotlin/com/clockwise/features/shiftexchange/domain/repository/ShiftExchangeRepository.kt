package com.clockwise.features.shiftexchange.domain.repository

import com.clockwise.features.shiftexchange.domain.model.ExchangeShift
import com.clockwise.features.shiftexchange.domain.model.ShiftRequest
import com.clockwise.features.shiftexchange.domain.model.RequestType
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface ShiftExchangeRepository {
    
    /**
     * Post a shift to the marketplace for exchange
     */
    suspend fun postShiftToMarketplace(
        planningServiceShiftId: String,
        businessUnitId: String,
        shiftPosition: String,
        shiftStartTime: String,
        shiftEndTime: String,
        userFirstName: String,
        userLastName: String
    ): Flow<Result<ExchangeShift, DataError.Remote>>
    
    /**
     * Get available shifts from the marketplace
     */
    suspend fun getAvailableShifts(
        businessUnitId: String,
        page: Int = 0,
        size: Int = 20
    ): Flow<Result<List<ExchangeShift>, DataError.Remote>>
    
    /**
     * Get user's posted exchange shifts
     */
    suspend fun getMyPostedShifts(): Flow<Result<List<ExchangeShift>, DataError.Remote>>
    
    /**
     * Submit a request to take or swap a shift
     */
    suspend fun submitShiftRequest(
        exchangeShiftId: String,
        requestType: RequestType,
        swapShiftId: String? = null,
        swapShiftPosition: String? = null,
        swapShiftStartTime: String? = null,
        swapShiftEndTime: String? = null,
        requesterUserFirstName: String? = null,
        requesterUserLastName: String? = null
    ): Flow<Result<ShiftRequest, DataError.Remote>>
    
    /**
     * Get user's submitted shift requests
     */
    suspend fun getMyRequests(): Flow<Result<List<ShiftRequest>, DataError.Remote>>
    
    /**
     * Get incoming requests for user's posted shifts
     */
    suspend fun getRequestsForMyShift(
        exchangeShiftId: String
    ): Flow<Result<List<ShiftRequest>, DataError.Remote>>
    
    /**
     * Accept an incoming request for a posted shift
     */
    suspend fun acceptRequest(
        exchangeShiftId: String,
        requestId: String
    ): Flow<Result<ShiftRequest, DataError.Remote>>
    
    /**
     * Cancel a posted exchange shift
     */
    suspend fun cancelExchangeShift(
        exchangeShiftId: String
    ): Flow<Result<ExchangeShift, DataError.Remote>>
}