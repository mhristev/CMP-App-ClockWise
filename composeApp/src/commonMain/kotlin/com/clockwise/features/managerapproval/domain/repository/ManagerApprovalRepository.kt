package com.clockwise.features.managerapproval.domain.repository

import com.clockwise.features.managerapproval.domain.model.PendingExchangeShift
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface ManagerApprovalRepository {
    suspend fun getPendingExchanges(): Result<List<PendingExchangeShift>, DataError>
    suspend fun approveExchange(requestId: String): Result<Unit, DataError>
    suspend fun rejectExchange(requestId: String): Result<Unit, DataError>
    suspend fun recheckConflicts(requestId: String, existingExchange: PendingExchangeShift): Result<PendingExchangeShift, DataError>
}