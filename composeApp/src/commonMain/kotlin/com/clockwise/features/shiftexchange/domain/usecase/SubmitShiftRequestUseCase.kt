package com.clockwise.features.shiftexchange.domain.usecase

import com.clockwise.features.shiftexchange.domain.model.RequestType
import com.clockwise.features.shiftexchange.domain.model.ShiftRequest
import com.clockwise.features.shiftexchange.domain.repository.ShiftExchangeRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

class SubmitShiftRequestUseCase(
    private val repository: ShiftExchangeRepository
) {
    suspend operator fun invoke(
        exchangeShiftId: String,
        requestType: RequestType,
        swapShiftId: String? = null,
        swapShiftPosition: String? = null,
        swapShiftStartTime: String? = null,
        swapShiftEndTime: String? = null,
        requesterUserFirstName: String? = null,
        requesterUserLastName: String? = null
    ): Flow<Result<ShiftRequest, DataError.Remote>> {
        return repository.submitShiftRequest(
            exchangeShiftId = exchangeShiftId,
            requestType = requestType,
            swapShiftId = swapShiftId,
            swapShiftPosition = swapShiftPosition,
            swapShiftStartTime = swapShiftStartTime,
            swapShiftEndTime = swapShiftEndTime,
            requesterUserFirstName = requesterUserFirstName,
            requesterUserLastName = requesterUserLastName
        )
    }
}