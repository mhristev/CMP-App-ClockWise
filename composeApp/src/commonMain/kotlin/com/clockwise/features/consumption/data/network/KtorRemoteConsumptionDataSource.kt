package com.clockwise.features.consumption.data.network

import com.clockwise.core.di.ApiConfig
import com.clockwise.features.consumption.data.dto.ConsumptionItemDto
import com.clockwise.features.consumption.data.dto.BulkCreateConsumptionRecordDto
import com.clockwise.features.consumption.data.dto.ConsumptionRecordDto
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
    
    override suspend fun recordBulkConsumption(bulkDto: BulkCreateConsumptionRecordDto): Result<List<ConsumptionRecordDto>, DataError.Remote> {
        return try {
            val url = "${apiConfig.baseOrganizationUrl}/consumption-records/bulk"
            println("🔍 DEBUG KtorRemoteConsumptionDataSource: Recording bulk consumption to $url")
            println("🔍 DEBUG bulkDto: workSessionId=${bulkDto.workSessionId}, consumptions=${bulkDto.consumptions.size} items")
            
            val response = httpClient.post {
                url(url)
                contentType(ContentType.Application.Json)
                setBody(bulkDto)
            }
            
            println("🔍 DEBUG KtorRemoteConsumptionDataSource: Bulk consumption response status = ${response.status}")
            
            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                val records = response.body<List<ConsumptionRecordDto>>()
                println("🔍 DEBUG KtorRemoteConsumptionDataSource: Successfully recorded ${records.size} consumption records")
                Result.Success(records)
            } else {
                println("🔍 DEBUG KtorRemoteConsumptionDataSource: Bulk consumption error - HTTP ${response.status}")
                Result.Error(DataError.Remote.UNKNOWN)
            }
        } catch (e: Exception) {
            println("🔍 DEBUG KtorRemoteConsumptionDataSource: Bulk consumption exception - ${e.message}")
            e.printStackTrace()
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
}