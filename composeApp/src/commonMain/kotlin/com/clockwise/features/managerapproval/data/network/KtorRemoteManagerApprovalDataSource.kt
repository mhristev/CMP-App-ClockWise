package com.clockwise.features.managerapproval.data.network

import com.clockwise.core.di.ApiConfig
import com.clockwise.features.auth.UserService
import com.clockwise.features.managerapproval.data.dto.PendingExchangeShiftDto
import com.clockwise.features.managerapproval.data.dto.ShiftRequestDto as ManagerApprovalShiftRequestDto
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put

class KtorRemoteManagerApprovalDataSource(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig,
    private val userService: UserService
) : RemoteManagerApprovalDataSource {

    override suspend fun getPendingExchanges(): Result<List<PendingExchangeShiftDto>, DataError.Remote> {
        val currentUser = userService.currentUser.value
        val businessUnitId = currentUser?.businessUnitId
        
        if (businessUnitId == null) {
            println("❌ getPendingExchanges: No business unit ID found for current user")
            return Result.Error(DataError.Remote.UNKNOWN)
        }
        
        println("🔍 getPendingExchanges: Using business unit ID: $businessUnitId")
        
        return safeCall {
            httpClient.get("${apiConfig.baseCollaborationUrl}/marketplace/manager/awaiting-approval") {
                parameter("businessUnitId", businessUnitId)
            }
        }
    }

    override suspend fun approveExchange(requestId: String): Result<Unit, DataError.Remote> {
        return safeCall {
            httpClient.put("${apiConfig.baseCollaborationUrl}/marketplace/manager/requests/$requestId/approve")
        }
    }

    override suspend fun rejectExchange(requestId: String): Result<Unit, DataError.Remote> {
        return safeCall {
            httpClient.put("${apiConfig.baseCollaborationUrl}/marketplace/manager/requests/$requestId/reject")
        }
    }

    override suspend fun recheckConflicts(requestId: String): Result<ManagerApprovalShiftRequestDto, DataError.Remote> {
        println("🔄 recheckConflicts: Rechecking conflicts for request ID: $requestId")
        return safeCall {
            httpClient.post("${apiConfig.baseCollaborationUrl}/marketplace/manager/requests/$requestId/recheck-conflicts")
        }
    }
}