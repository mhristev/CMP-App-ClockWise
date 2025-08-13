package com.clockwise.features.collaboration.domain.repository

import com.clockwise.features.collaboration.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface CollaborationRepository {
    suspend fun getPostsForBusinessUnit(
        businessUnitId: String,
        page: Int = 0,
        size: Int = 20
    ): Flow<List<Post>>
    
    suspend fun getPostById(postId: String): Post?
}