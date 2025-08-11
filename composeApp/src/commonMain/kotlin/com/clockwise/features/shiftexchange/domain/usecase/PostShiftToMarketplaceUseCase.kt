package com.clockwise.features.shiftexchange.domain.usecase

import com.clockwise.features.shiftexchange.domain.model.ExchangeShift
import com.clockwise.features.shiftexchange.domain.repository.ShiftExchangeRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

class PostShiftToMarketplaceUseCase(
    private val repository: ShiftExchangeRepository
) {
    suspend operator fun invoke(
        planningServiceShiftId: String,
        businessUnitId: String,
        shiftPosition: String,
        shiftStartTime: String,
        shiftEndTime: String,
        userFirstName: String,
        userLastName: String
    ): Flow<Result<ExchangeShift, DataError.Remote>> {
        return repository.postShiftToMarketplace(
            planningServiceShiftId = planningServiceShiftId,
            businessUnitId = businessUnitId,
            shiftPosition = shiftPosition,
            shiftStartTime = shiftStartTime,
            shiftEndTime = shiftEndTime,
            userFirstName = userFirstName,
            userLastName = userLastName
        )
    }
}