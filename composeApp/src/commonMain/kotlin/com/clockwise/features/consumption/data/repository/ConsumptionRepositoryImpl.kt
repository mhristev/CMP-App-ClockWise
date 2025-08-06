package com.clockwise.features.consumption.data.repository

import com.clockwise.features.consumption.data.network.RemoteConsumptionDataSource
import com.clockwise.features.consumption.data.dto.BulkCreateConsumptionRecordDto
import com.clockwise.features.consumption.data.dto.ConsumptionItemUsageDto
import com.clockwise.features.consumption.domain.repository.ConsumptionRepository
import com.clockwise.features.consumption.domain.model.ConsumptionItem
import com.clockwise.features.consumption.domain.model.SelectedConsumptionItem
import com.plcoding.bookpedia.core.domain.Result

class ConsumptionRepositoryImpl(
    private val remoteDataSource: RemoteConsumptionDataSource
) : ConsumptionRepository {
    
    override suspend fun getConsumptionItemsByBusinessUnit(businessUnitId: String): Result<List<ConsumptionItem>, com.plcoding.bookpedia.core.domain.DataError.Remote> {
        return when (val result = remoteDataSource.getConsumptionItemsByBusinessUnit(businessUnitId)) {
            is Result.Success -> {
                val consumptionItems = result.data.map { dto ->
                    ConsumptionItem(
                        id = dto.id,
                        name = dto.name,
                        price = dto.price,
                        type = dto.type,
                        businessUnitId = dto.businessUnitId,
                        createdAt = dto.createdAt,
                        updatedAt = dto.updatedAt
                    )
                }
                Result.Success(consumptionItems)
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun recordBulkConsumption(workSessionId: String, selectedItems: List<SelectedConsumptionItem>): Result<Unit, com.plcoding.bookpedia.core.domain.DataError.Remote> {
        if (selectedItems.isEmpty()) {
            return Result.Success(Unit) // No items to record
        }
        
        val bulkDto = BulkCreateConsumptionRecordDto(
            workSessionId = workSessionId,
            consumptions = selectedItems.map { selectedItem ->
                ConsumptionItemUsageDto(
                    consumptionItemId = selectedItem.consumptionItem.id,
                    quantity = selectedItem.quantity.toDouble()
                )
            }
        )
        
        return when (val result = remoteDataSource.recordBulkConsumption(bulkDto)) {
            is Result.Success -> {
                println("🔍 DEBUG ConsumptionRepositoryImpl: Successfully recorded ${result.data.size} consumption records")
                Result.Success(Unit)
            }
            is Result.Error -> {
                println("🔍 DEBUG ConsumptionRepositoryImpl: Failed to record bulk consumption - ${result.error}")
                result
            }
        }
    }
}