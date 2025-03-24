package com.example.clockwise.data.remote

import com.example.clockwise.data.model.Company
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

class CompanyDataSourceImpl(
    private val httpClient: HttpClient,
    private val json: Json
) : CompanyDataSource {
    override suspend fun getCompanies(): List<Company> {
        val response = httpClient.get("http://127.0.0.1:8001/v1/companies")
        return json.decodeFromString(response.bodyAsText())
    }
} 