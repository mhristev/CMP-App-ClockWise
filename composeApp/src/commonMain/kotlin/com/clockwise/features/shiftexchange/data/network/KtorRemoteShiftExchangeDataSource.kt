package com.clockwise.features.shiftexchange.data.network

import com.clockwise.core.di.ApiConfig
import com.clockwise.features.shiftexchange.data.dto.CreateExchangeShiftRequest
import com.clockwise.features.shiftexchange.data.dto.CreateShiftRequestRequest
import com.clockwise.features.shiftexchange.data.dto.ExchangeShiftDto
import com.clockwise.features.shiftexchange.data.dto.ExchangeShiftListResponse
import com.clockwise.features.shiftexchange.data.dto.ShiftRequestDto
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.HttpStatusCode
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.isSuccess

class KtorRemoteShiftExchangeDataSource(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) : RemoteShiftExchangeDataSource {
    
    override suspend fun postShiftToMarketplace(
        planningServiceShiftId: String,
        request: CreateExchangeShiftRequest
    ): Result<ExchangeShiftDto, DataError.Remote> {
        return try {
            val response = httpClient.post("${apiConfig.baseCollaborationUrl}/shifts/$planningServiceShiftId") {
                setBody(request)
            }
            
            when {
                response.status.isSuccess() -> {
                    val exchangeShift = response.body<ExchangeShiftDto>()
                    Result.Success(exchangeShift)
                }
                response.status == HttpStatusCode.Conflict -> {
                    Result.Error(DataError.Remote.REQUEST_TIMEOUT)
                }
                else -> {
                    Result.Error(DataError.Remote.UNKNOWN)
                }
            }
        } catch (e: ClientRequestException) {
            Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (e: ServerResponseException) {
            Result.Error(DataError.Remote.SERVER)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
    
    override suspend fun getAvailableShifts(
        businessUnitId: String,
        page: Int,
        size: Int
    ): Result<ExchangeShiftListResponse, DataError.Remote> {
        return try {
            val url = "${apiConfig.baseCollaborationUrl}/shifts"
            println("DEBUG: getAvailableShifts - URL: $url")
            println("DEBUG: getAvailableShifts - BusinessUnitId: $businessUnitId")
            println("DEBUG: getAvailableShifts - Page: $page, Size: $size")
            
            val response = httpClient.get(url) {
                parameter("businessUnitId", businessUnitId)
                parameter("page", page)
                parameter("size", size)
            }
            
            println("DEBUG: getAvailableShifts - Response status: ${response.status}")
            
            when {
                response.status.isSuccess() -> {
                    val result = response.body<ExchangeShiftListResponse>()
                    println("DEBUG: getAvailableShifts - Success: ${result.exchangeShifts.size} shifts found")
                    Result.Success(result)
                }
                else -> {
                    println("DEBUG: getAvailableShifts - Error: ${response.status}")
                    Result.Error(DataError.Remote.UNKNOWN)
                }
            }
        } catch (e: ClientRequestException) {
            println("DEBUG: getAvailableShifts - ClientRequestException: ${e.message}")
            Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (e: ServerResponseException) {
            println("DEBUG: getAvailableShifts - ServerResponseException: ${e.message}")
            Result.Error(DataError.Remote.SERVER)
        } catch (e: Exception) {
            println("DEBUG: getAvailableShifts - Exception: ${e.message}")
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
    
    override suspend fun getMyPostedShifts(): Result<List<ExchangeShiftDto>, DataError.Remote> {
        return try {
            val response = httpClient.get("${apiConfig.baseCollaborationUrl}/my-shifts")
            
            when {
                response.status.isSuccess() -> {
                    val shifts = response.body<List<ExchangeShiftDto>>()
                    Result.Success(shifts)
                }
                else -> Result.Error(DataError.Remote.UNKNOWN)
            }
        } catch (e: ClientRequestException) {
            Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (e: ServerResponseException) {
            Result.Error(DataError.Remote.SERVER)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
    
    override suspend fun submitShiftRequest(
        exchangeShiftId: String,
        request: CreateShiftRequestRequest
    ): Result<ShiftRequestDto, DataError.Remote> {
        return try {
            val url = "${apiConfig.baseCollaborationUrl}/shifts/$exchangeShiftId/requests"
            println("DEBUG: submitShiftRequest - URL: $url")
            println("DEBUG: submitShiftRequest - ExchangeShiftId: $exchangeShiftId")
            println("DEBUG: submitShiftRequest - Request: $request")
            println("DEBUG: submitShiftRequest - RequestType: ${request.requestType}")
            println("DEBUG: submitShiftRequest - SwapShiftId: ${request.swapShiftId}")
            println("DEBUG: submitShiftRequest - SwapShiftStartTime: ${request.swapShiftStartTime}")
            println("DEBUG: submitShiftRequest - SwapShiftEndTime: ${request.swapShiftEndTime}")
            println("DEBUG: submitShiftRequest - RequesterUserFirstName: ${request.requesterUserFirstName}")
            println("DEBUG: submitShiftRequest - RequesterUserLastName: ${request.requesterUserLastName}")
            
            val response = httpClient.post(url) {
                setBody(request)
            }
            
            println("DEBUG: submitShiftRequest - Response status: ${response.status}")
            
            when {
                response.status.isSuccess() -> {
                    val shiftRequest = response.body<ShiftRequestDto>()
                    println("DEBUG: submitShiftRequest - Success: ${shiftRequest.id}")
                    Result.Success(shiftRequest)
                }
                response.status == HttpStatusCode.BadRequest -> {
                    println("DEBUG: submitShiftRequest - Bad Request (400)")
                    try {
                        val errorBody = response.body<String>()
                        println("DEBUG: submitShiftRequest - Error body: '$errorBody'")
                        println("DEBUG: submitShiftRequest - Error body length: ${errorBody.length}")
                    } catch (e: Exception) {
                        println("DEBUG: submitShiftRequest - Could not read error body: ${e.message}")
                        println("DEBUG: submitShiftRequest - Exception type: ${e::class.simpleName}")
                    }
                    Result.Error(DataError.Remote.REQUEST_TIMEOUT)
                }
                response.status == HttpStatusCode.NotFound -> {
                    println("DEBUG: submitShiftRequest - Not Found (404) - Server routing issue")
                    try {
                        val errorBody = response.body<String>()
                        println("DEBUG: submitShiftRequest - 404 Error body: '$errorBody'")
                    } catch (e: Exception) {
                        println("DEBUG: submitShiftRequest - Could not read 404 error body: ${e.message}")
                    }
                    Result.Error(DataError.Remote.UNKNOWN)
                }
                response.status.value in 500..599 -> {
                    println("DEBUG: submitShiftRequest - Server Error (${response.status})")
                    try {
                        val errorBody = response.body<String>()
                        println("DEBUG: submitShiftRequest - Server error body: '$errorBody'")
                    } catch (e: Exception) {
                        println("DEBUG: submitShiftRequest - Could not read server error body: ${e.message}")
                    }
                    Result.Error(DataError.Remote.SERVER)
                }
                else -> {
                    println("DEBUG: submitShiftRequest - Other error: ${response.status}")
                    Result.Error(DataError.Remote.UNKNOWN)
                }
            }
        } catch (e: ClientRequestException) {
            println("DEBUG: submitShiftRequest - ClientRequestException: ${e.message}")
            Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (e: ServerResponseException) {
            println("DEBUG: submitShiftRequest - ServerResponseException: ${e.message}")
            Result.Error(DataError.Remote.SERVER)
        } catch (e: Exception) {
            println("DEBUG: submitShiftRequest - Exception: ${e.message}")
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
    
    override suspend fun getMyRequests(): Result<List<ShiftRequestDto>, DataError.Remote> {
        return try {
            val response = httpClient.get("${apiConfig.baseCollaborationUrl}/my-requests")
            
            when {
                response.status.isSuccess() -> {
                    val requests = response.body<List<ShiftRequestDto>>()
                    Result.Success(requests)
                }
                else -> Result.Error(DataError.Remote.UNKNOWN)
            }
        } catch (e: ClientRequestException) {
            Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (e: ServerResponseException) {
            Result.Error(DataError.Remote.SERVER)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
    
    override suspend fun getRequestsForMyShift(
        exchangeShiftId: String
    ): Result<List<ShiftRequestDto>, DataError.Remote> {
        return try {
            val response = httpClient.get("${apiConfig.baseCollaborationUrl}/my-shifts/$exchangeShiftId/requests")
            
            when {
                response.status.isSuccess() -> {
                    val requests = response.body<List<ShiftRequestDto>>()
                    Result.Success(requests)
                }
                else -> Result.Error(DataError.Remote.UNKNOWN)
            }
        } catch (e: ClientRequestException) {
            Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (e: ServerResponseException) {
            Result.Error(DataError.Remote.SERVER)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
    
    override suspend fun acceptRequest(
        exchangeShiftId: String,
        requestId: String
    ): Result<ShiftRequestDto, DataError.Remote> {
        return try {
            val response = httpClient.put("${apiConfig.baseCollaborationUrl}/my-shifts/$exchangeShiftId/requests/$requestId/accept")
            
            when {
                response.status.isSuccess() -> {
                    val shiftRequest = response.body<ShiftRequestDto>()
                    Result.Success(shiftRequest)
                }
                response.status == HttpStatusCode.BadRequest -> {
                    Result.Error(DataError.Remote.REQUEST_TIMEOUT)
                }
                else -> Result.Error(DataError.Remote.UNKNOWN)
            }
        } catch (e: ClientRequestException) {
            Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (e: ServerResponseException) {
            Result.Error(DataError.Remote.SERVER)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
    
    override suspend fun cancelExchangeShift(
        exchangeShiftId: String
    ): Result<ExchangeShiftDto, DataError.Remote> {
        return try {
            val response = httpClient.delete("${apiConfig.baseCollaborationUrl}/my-shifts/$exchangeShiftId")
            
            when {
                response.status.isSuccess() -> {
                    val exchangeShift = response.body<ExchangeShiftDto>()
                    Result.Success(exchangeShift)
                }
                response.status == HttpStatusCode.BadRequest -> {
                    Result.Error(DataError.Remote.REQUEST_TIMEOUT)
                }
                else -> Result.Error(DataError.Remote.UNKNOWN)
            }
        } catch (e: ClientRequestException) {
            Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (e: ServerResponseException) {
            Result.Error(DataError.Remote.SERVER)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
}