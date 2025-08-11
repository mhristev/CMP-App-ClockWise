package com.clockwise.features.shiftexchange.domain.usecase

import com.clockwise.features.shiftexchange.domain.model.ShiftRequest
import com.clockwise.features.shiftexchange.domain.repository.ShiftExchangeRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

class AcceptRequestUseCase(
    private val repository: ShiftExchangeRepository
) {
    suspend operator fun invoke(
        exchangeShiftId: String,
        requestId: String
    ): Flow<Result<ShiftRequest, DataError.Remote>> {
        return repository.acceptRequest(exchangeShiftId, requestId)
    }
}