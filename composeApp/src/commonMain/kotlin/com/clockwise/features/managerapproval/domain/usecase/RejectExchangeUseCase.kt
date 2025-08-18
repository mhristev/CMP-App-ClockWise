package com.clockwise.features.managerapproval.domain.usecase

import com.clockwise.features.managerapproval.domain.repository.ManagerApprovalRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

class RejectExchangeUseCase(
    private val repository: ManagerApprovalRepository
) {
    suspend fun execute(requestId: String): Result<Unit, DataError> {
        return repository.rejectExchange(requestId)
    }
}