package com.clockwise.features.shiftexchange.domain.usecase

import com.clockwise.features.shiftexchange.domain.model.ShiftRequest
import com.clockwise.features.shiftexchange.domain.repository.ShiftExchangeRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

class GetRequestsForMyShiftUseCase(
    private val repository: ShiftExchangeRepository
) {
    suspend operator fun invoke(
        exchangeShiftId: String
    ): Flow<Result<List<ShiftRequest>, DataError.Remote>> {
        return repository.getRequestsForMyShift(exchangeShiftId)
    }
}