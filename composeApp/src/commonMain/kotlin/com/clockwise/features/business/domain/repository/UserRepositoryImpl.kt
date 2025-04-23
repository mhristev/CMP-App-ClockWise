package com.clockwise.features.business.domain.repository

import com.clockwise.core.di.ApiConfig
import com.clockwise.features.business.data.repository.UserRepository
import com.clockwise.features.business.presentation.add_employee.User
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable

// Remove hardcoded URLs
// private const val BASE_URL = "http://10.0.2.2:8081/v1/users/without-business-unit"
// private const val USERS_URL = "http://10.0.2.2:8081/v1/users"

@Serializable
data class UpdateBusinessUnitRequest(val businessUnitId: String)

class UserRepositoryImpl(
    private val client: HttpClient,
    private val apiConfig: ApiConfig
) : UserRepository {
    override suspend fun searchUsers(query: String): Flow<Result<List<User>, DataError.Remote>> {
        return flow {
            val result = safeCall<List<User>> {
                client.get("${apiConfig.baseUsersUrl}/without-business-unit")
            }
            emit(result)
        }
    }
    
    override suspend fun addUserToBusinessUnit(userId: String, businessUnitId: String): Flow<Result<Unit, DataError.Remote>> {
        return flow {
            val result = safeCall<Unit> {
                client.put("${apiConfig.baseUsersUrl}/$userId/business-unit") {
                    contentType(ContentType.Application.Json)
                    setBody(UpdateBusinessUnitRequest(businessUnitId))
                }
            }
            emit(result)
        }
    }
} 