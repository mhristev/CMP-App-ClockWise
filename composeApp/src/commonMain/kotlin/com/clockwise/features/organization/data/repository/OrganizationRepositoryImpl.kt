package com.clockwise.features.organization.data.repository

import com.clockwise.core.di.ApiConfig
import com.clockwise.features.organization.data.model.BusinessUnitAddress
import com.clockwise.features.organization.data.model.BusinessUnitAddressDto
import com.clockwise.features.organization.data.model.toDomain
import com.clockwise.features.organization.domain.repository.OrganizationRepository
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.map
import io.ktor.client.*
import io.ktor.client.request.*

class OrganizationRepositoryImpl(
    private val client: HttpClient,
    private val apiConfig: ApiConfig
) : OrganizationRepository {

    override suspend fun getBusinessUnitById(businessUnitId: String): Result<BusinessUnitAddress, DataError.Remote> {
        return safeCall<BusinessUnitAddressDto> {
            client.get("${apiConfig.baseOrganizationUrl}/business-units/$businessUnitId/address")
        }.map { dto ->
            dto.toDomain()
        }
    }
}
