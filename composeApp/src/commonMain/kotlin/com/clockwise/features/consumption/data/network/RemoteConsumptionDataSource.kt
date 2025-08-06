package com.clockwise.features.consumption.data.network

import com.clockwise.features.consumption.data.dto.ConsumptionItemDto
import com.clockwise.features.consumption.data.dto.BulkCreateConsumptionRecordDto
import com.clockwise.features.consumption.data.dto.ConsumptionRecordDto
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.DataError

interface RemoteConsumptionDataSource {
    suspend fun getConsumptionItemsByBusinessUnit(businessUnitId: String): Result<List<ConsumptionItemDto>, DataError.Remote>
    suspend fun recordBulkConsumption(bulkDto: BulkCreateConsumptionRecordDto): Result<List<ConsumptionRecordDto>, DataError.Remote>
}