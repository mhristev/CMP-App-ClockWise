package com.clockwise.features.consumption.data.network

import com.clockwise.core.di.ApiConfig
import com.clockwise.features.consumption.data.dto.ConsumptionItemDto
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.DataError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class KtorRemoteConsumptionDataSource(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) : RemoteConsumptionDataSource {
    
    override suspend fun getConsumptionItemsByBusinessUnit(businessUnitId: String): Result<List<ConsumptionItemDto>, DataError.Remote> {
        return try {
            val url = "${apiConfig.baseOrganizationUrl}/business-units/$businessUnitId/consumption-items"
            println("DEBUG KtorRemoteConsumptionDataSource: Making request to $url")
            
            val response = httpClient.get {
                url(url)
            }
            
            println("DEBUG KtorRemoteConsumptionDataSource: Response status = ${response.status}")
            
            if (response.status == HttpStatusCode.OK) {
                val items = response.body<List<ConsumptionItemDto>>()
                println("DEBUG KtorRemoteConsumptionDataSource: Successfully loaded ${items.size} items")
                Result.Success(items)
            } else {
                println("DEBUG KtorRemoteConsumptionDataSource: Error - HTTP ${response.status}")
                Result.Error(DataError.Remote.UNKNOWN)
            }
        } catch (e: Exception) {
            println("DEBUG KtorRemoteConsumptionDataSource: Exception - ${e.message}")
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
}