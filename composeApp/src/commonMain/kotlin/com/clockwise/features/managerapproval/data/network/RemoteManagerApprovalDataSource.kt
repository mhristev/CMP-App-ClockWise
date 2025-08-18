package com.clockwise.features.managerapproval.data.network

import com.clockwise.features.managerapproval.data.dto.PendingExchangeShiftDto
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface RemoteManagerApprovalDataSource {
    suspend fun getPendingExchanges(): Result<List<PendingExchangeShiftDto>, DataError.Remote>
    suspend fun approveExchange(requestId: String): Result<Unit, DataError.Remote>
    suspend fun rejectExchange(requestId: String): Result<Unit, DataError.Remote>
}