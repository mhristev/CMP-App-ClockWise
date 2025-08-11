package com.clockwise.features.shiftexchange.domain.usecase

import com.clockwise.features.shiftexchange.domain.model.ExchangeShift
import com.clockwise.features.shiftexchange.domain.repository.ShiftExchangeRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

class GetAvailableShiftsUseCase(
    private val repository: ShiftExchangeRepository
) {
    suspend operator fun invoke(
        businessUnitId: String,
        page: Int = 0,
        size: Int = 20
    ): Flow<Result<List<ExchangeShift>, DataError.Remote>> {
        println("DEBUG: GetAvailableShiftsUseCase - INVOKE called with businessUnitId: $businessUnitId")
        println("DEBUG: GetAvailableShiftsUseCase - About to call repository.getAvailableShifts() for marketplace shifts")
        return repository.getAvailableShifts(businessUnitId, page, size)
    }   
}