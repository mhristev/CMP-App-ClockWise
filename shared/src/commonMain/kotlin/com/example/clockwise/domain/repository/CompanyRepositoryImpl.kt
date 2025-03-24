package com.example.clockwise.domain.repository

import com.example.clockwise.data.model.Company
import com.example.clockwise.data.remote.CompanyDataSource

class CompanyRepositoryImpl(
    private val dataSource: CompanyDataSource
) : CompanyRepository {
    override suspend fun getCompanies(): List<Company> {
        return dataSource.getCompanies()
    }
} 