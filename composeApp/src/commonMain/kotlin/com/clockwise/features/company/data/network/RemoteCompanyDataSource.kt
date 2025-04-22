package com.clockwise.features.company.data.network


import kotlinx.coroutines.flow.Flow

import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.DataError

interface RemoteCompanyDataSource {
    suspend fun getCompanies(): Flow<Result<List<CompanyDto>, DataError.Remote>>
}