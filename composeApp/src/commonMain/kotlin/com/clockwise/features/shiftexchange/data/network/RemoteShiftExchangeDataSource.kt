package com.clockwise.features.shiftexchange.data.network

import com.clockwise.features.shiftexchange.data.dto.CreateExchangeShiftRequest
import com.clockwise.features.shiftexchange.data.dto.CreateShiftRequestRequest
import com.clockwise.features.shiftexchange.data.dto.ExchangeShiftDto
import com.clockwise.features.shiftexchange.data.dto.ExchangeShiftListResponse
import com.clockwise.features.shiftexchange.data.dto.ShiftRequestDto
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface RemoteShiftExchangeDataSource {
    
    suspend fun postShiftToMarketplace(
        planningServiceShiftId: String,
        request: CreateExchangeShiftRequest
    ): Result<ExchangeShiftDto, DataError.Remote>
    
    suspend fun getAvailableShifts(
        businessUnitId: String,
        page: Int = 0,
        size: Int = 20
    ): Result<ExchangeShiftListResponse, DataError.Remote>
    
    suspend fun getMyPostedShifts(): Result<List<ExchangeShiftDto>, DataError.Remote>
    
    suspend fun submitShiftRequest(
        exchangeShiftId: String,
        request: CreateShiftRequestRequest
    ): Result<ShiftRequestDto, DataError.Remote>
    
    suspend fun getMyRequests(): Result<List<ShiftRequestDto>, DataError.Remote>
    
    suspend fun getRequestsForMyShift(
        exchangeShiftId: String
    ): Result<List<ShiftRequestDto>, DataError.Remote>
    
    suspend fun acceptRequest(
        exchangeShiftId: String,
        requestId: String
    ): Result<ShiftRequestDto, DataError.Remote>
    
    suspend fun cancelExchangeShift(
        exchangeShiftId: String
    ): Result<ExchangeShiftDto, DataError.Remote>
}