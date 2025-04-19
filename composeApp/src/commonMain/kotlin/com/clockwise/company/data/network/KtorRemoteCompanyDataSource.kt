package com.clockwise.company.data.network

import com.clockwise.company.domain.Company
import com.clockwise.core.di.ApiConfig
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import com.plcoding.bookpedia.core.domain.Result

// Remove hardcoded BASE_URL
// private const val BASE_URL = "http://10.0.2.2:8080/v1/companies"

@Serializable
data class CompanyDto(
    val id: String,
    val name: String,
    val description: String
)
@Serializable
data class GetCompaniesResponse(
    val companies: List<CompanyDto>
)

fun CompanyDto.to(): Company {
    return Company(
        id = id,
        name = name,
        description = description
    )
}
class KtorRemoteCompanyDataSource(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
): RemoteCompanyDataSource {
    override suspend fun getCompanies(): Flow<Result<List<CompanyDto>, DataError.Remote>> {
        return flow {
            val result = safeCall<List<CompanyDto>> {
                httpClient.get(apiConfig.baseCompaniesUrl)
            }
            emit(result)
        }
    }
}