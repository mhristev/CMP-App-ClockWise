package com.clockwise.features.consumption.data.repository

import com.clockwise.features.consumption.data.network.RemoteConsumptionDataSource
import com.clockwise.features.consumption.domain.repository.ConsumptionRepository
import com.clockwise.features.consumption.domain.model.ConsumptionItem
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
}