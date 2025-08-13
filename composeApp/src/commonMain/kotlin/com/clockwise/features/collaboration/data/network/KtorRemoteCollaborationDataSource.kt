package com.clockwise.features.collaboration.data.network

import com.clockwise.core.di.ApiConfig
import com.clockwise.features.collaboration.data.dto.PostDto
import com.clockwise.features.collaboration.data.dto.PostListResponse
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class KtorRemoteCollaborationDataSource(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) : RemoteCollaborationDataSource {

    override suspend fun getPostsForBusinessUnit(
        businessUnitId: String,
        page: Int,
        size: Int
    ): Result<PostListResponse, DataError.Remote> {
        return safeCall {
            httpClient.get("${apiConfig.baseCollaborationUrl}/posts") {
                parameter("businessUnitId", businessUnitId)
                parameter("page", page)
                parameter("size", size)
            }
        }
    }

    override suspend fun getPostById(postId: String): Result<PostDto, DataError.Remote> {
        return safeCall {
            httpClient.get("${apiConfig.baseCollaborationUrl}/posts/$postId")
        }
    }
}