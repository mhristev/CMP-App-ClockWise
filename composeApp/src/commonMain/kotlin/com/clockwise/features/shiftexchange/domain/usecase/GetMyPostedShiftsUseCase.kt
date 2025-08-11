package com.clockwise.features.shiftexchange.domain.usecase

import com.clockwise.features.shiftexchange.domain.model.ExchangeShift
import com.clockwise.features.shiftexchange.domain.repository.ShiftExchangeRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

class GetMyPostedShiftsUseCase(
    private val repository: ShiftExchangeRepository
) {
    suspend operator fun invoke(): Flow<Result<List<ExchangeShift>, DataError.Remote>> {
        return repository.getMyPostedShifts()
    }
}