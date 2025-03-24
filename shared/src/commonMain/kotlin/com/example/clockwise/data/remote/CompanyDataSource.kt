package com.example.clockwise.data.remote

import com.example.clockwise.data.model.Company

interface CompanyDataSource {
    suspend fun getCompanies(): List<Company>
} 