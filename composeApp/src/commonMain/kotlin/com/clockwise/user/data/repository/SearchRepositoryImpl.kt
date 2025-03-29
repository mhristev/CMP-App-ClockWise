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
private const val BASE_URL = "http://10.0.2.2:8081/v1/users/without-business-unit"
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
} 