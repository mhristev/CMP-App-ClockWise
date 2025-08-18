package com.clockwise.features.managerapproval.domain.usecase

import com.clockwise.features.managerapproval.domain.model.PendingExchangeShift
import com.clockwise.features.managerapproval.domain.repository.ManagerApprovalRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

class GetPendingExchangesUseCase(
    private val repository: ManagerApprovalRepository
) {
    suspend fun execute(): Result<List<PendingExchangeShift>, DataError> {
        return repository.getPendingExchanges()
    }
}