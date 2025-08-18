package com.clockwise.features.managerapproval.data.repository

import com.clockwise.features.managerapproval.data.network.RemoteManagerApprovalDataSource
import com.clockwise.features.managerapproval.domain.model.PendingExchangeShift
import com.clockwise.features.managerapproval.domain.model.RequestType
import com.clockwise.features.managerapproval.domain.repository.ManagerApprovalRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.map
import kotlinx.datetime.Instant

class ManagerApprovalRepositoryImpl(
    private val remoteDataSource: RemoteManagerApprovalDataSource
) : ManagerApprovalRepository {

    override suspend fun getPendingExchanges(): Result<List<PendingExchangeShift>, DataError> {
        return remoteDataSource.getPendingExchanges().map { dtoList ->
            dtoList.map { dto ->
                PendingExchangeShift(
                    id = dto.exchangeShift.id,
                    requestId = dto.acceptedRequest.id,
                    originalShiftId = dto.exchangeShift.planningServiceShiftId,
                    posterUserId = dto.exchangeShift.posterUserId,
                    posterUserFirstName = dto.exchangeShift.userFirstName,
                    posterUserLastName = dto.exchangeShift.userLastName,
                    requesterUserId = dto.acceptedRequest.requesterUserId,
                    requesterUserFirstName = dto.acceptedRequest.requesterUserFirstName,
                    requesterUserLastName = dto.acceptedRequest.requesterUserLastName,
                    shiftStartTime = dto.exchangeShift.shiftStartTime?.let { Instant.parse(it) },
                    shiftEndTime = dto.exchangeShift.shiftEndTime?.let { Instant.parse(it) },
                    shiftPosition = dto.exchangeShift.shiftPosition,
                    requestType = RequestType.valueOf(dto.acceptedRequest.requestType),
                    swapShiftId = dto.acceptedRequest.swapShiftId,
                    swapShiftStartTime = dto.acceptedRequest.swapShiftStartTime?.let { Instant.parse(it) },
                    swapShiftEndTime = dto.acceptedRequest.swapShiftEndTime?.let { Instant.parse(it) },
                    swapShiftPosition = dto.acceptedRequest.swapShiftPosition,
                    businessUnitId = dto.exchangeShift.businessUnitId,
                    createdAt = Instant.parse(dto.exchangeShift.createdAt)
                )
            }
        }
    }

    override suspend fun approveExchange(requestId: String): Result<Unit, DataError> {
        return remoteDataSource.approveExchange(requestId)
    }

    override suspend fun rejectExchange(requestId: String): Result<Unit, DataError> {
        return remoteDataSource.rejectExchange(requestId)
    }
}