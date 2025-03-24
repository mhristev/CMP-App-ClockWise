package com.clockwise.company.data.network

import com.clockwise.company.domain.Company
import com.plcoding.bookpedia.core.domain.DataError
import kotlinx.coroutines.flow.Flow
import com.plcoding.bookpedia.core.domain.Result

interface RemoteCompanyDataSource {
    suspend fun getCompanies(): Flow<Result<List<CompanyDto>, DataError.Remote>>
}