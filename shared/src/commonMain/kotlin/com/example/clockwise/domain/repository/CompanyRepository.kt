package com.example.clockwise.domain.repository

import com.example.clockwise.data.model.Company

interface CompanyRepository {
    suspend fun getCompanies(): List<Company>
} 