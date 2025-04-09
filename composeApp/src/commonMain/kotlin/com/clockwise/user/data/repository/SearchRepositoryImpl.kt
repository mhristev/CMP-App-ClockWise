package com.clockwise.user.data.repository

import com.clockwise.company.data.network.CompanyDto
import com.clockwise.user.domain.repository.SearchRepository
import com.clockwise.user.presentation.home.search.User
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

private const val BASE_URL = "http://10.0.2.2:8081/v1/users/without-business-unit"
private const val USERS_URL = "http://10.0.2.2:8081/v1/users"

@Serializable
data class UpdateBusinessUnitRequest(val businessUnitId: String)

class SearchRepositoryImpl(
    private val client: HttpClient
) : SearchRepository {
    override suspend fun searchUsers(query: String): Flow<Result<List<User>, DataError.Remote>> {
        return flow {
            val result = safeCall<List<User>> {
                client.get(BASE_URL)
            }
            emit(result)
        }
    }
    
    override suspend fun addUserToBusinessUnit(userId: String, businessUnitId: String): Flow<Result<Unit, DataError.Remote>> {
        return flow {
            val result = safeCall<Unit> {
                client.put("$USERS_URL/$userId/business-unit") {
                    contentType(ContentType.Application.Json)
                    setBody(UpdateBusinessUnitRequest(businessUnitId))
                }
            }
            emit(result)
        }
    }
} 