package com.clockwise.features.collaboration.data.repository

import com.clockwise.features.collaboration.data.network.RemoteCollaborationDataSource
import com.clockwise.features.collaboration.domain.model.Post
import com.clockwise.features.collaboration.domain.repository.CollaborationRepository
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CollaborationRepositoryImpl(
    private val remoteDataSource: RemoteCollaborationDataSource
) : CollaborationRepository {

    override suspend fun getPostsForBusinessUnit(
        businessUnitId: String,
        page: Int,
        size: Int
    ): Flow<List<Post>> = flow {
        when (val result = remoteDataSource.getPostsForBusinessUnit(
            businessUnitId = businessUnitId,
            page = page,
            size = size
        )) {
            is Result.Success -> {
                emit(result.data.posts.map { it.toDomainModel() })
            }
            is Result.Error -> {
                throw Exception("Failed to load posts: ${result.error}")
            }
        }
    }

    override suspend fun getPostById(postId: String): Post? {
        return when (val result = remoteDataSource.getPostById(postId)) {
            is Result.Success -> result.data.toDomainModel()
            is Result.Error -> null
        }
    }
}