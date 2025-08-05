package com.clockwise.features.consumption.domain.repository

import com.clockwise.features.consumption.domain.model.ConsumptionItem
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.DataError

interface ConsumptionRepository {
    suspend fun getConsumptionItemsByBusinessUnit(businessUnitId: String): Result<List<ConsumptionItem>, DataError.Remote>
}