package com.clockwise.features.managerapproval.domain.usecase

import com.clockwise.features.managerapproval.domain.model.PendingExchangeShift
import com.clockwise.features.managerapproval.domain.repository.ManagerApprovalRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

class RecheckConflictsUseCase(
    private val repository: ManagerApprovalRepository
) {
    suspend fun execute(requestId: String, existingExchange: PendingExchangeShift): Result<PendingExchangeShift, DataError> {
        return repository.recheckConflicts(requestId, existingExchange)
    }
}