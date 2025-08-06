package com.clockwise.features.organization.domain.repository

import com.clockwise.features.organization.data.model.BusinessUnit
import com.clockwise.features.organization.data.model.BusinessUnitAddress
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface OrganizationRepository {
    suspend fun getBusinessUnitById(businessUnitId: String): Result<BusinessUnitAddress, DataError.Remote>
    suspend fun getBusinessUnitDetails(businessUnitId: String): Result<BusinessUnit, DataError.Remote>
}
