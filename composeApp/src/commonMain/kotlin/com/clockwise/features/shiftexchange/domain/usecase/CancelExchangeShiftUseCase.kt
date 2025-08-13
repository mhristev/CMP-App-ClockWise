package com.clockwise.features.shiftexchange.domain.usecase

import com.clockwise.features.shiftexchange.domain.model.ExchangeShift
import com.clockwise.features.shiftexchange.domain.repository.ShiftExchangeRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

class CancelExchangeShiftUseCase(
    private val repository: ShiftExchangeRepository
) {
    suspend operator fun invoke(
        exchangeShiftId: String
    ): Flow<Result<ExchangeShift, DataError.Remote>> {
        return repository.cancelExchangeShift(exchangeShiftId)
    }
}