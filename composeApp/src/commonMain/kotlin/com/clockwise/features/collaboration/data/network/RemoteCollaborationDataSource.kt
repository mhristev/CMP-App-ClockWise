package com.clockwise.features.collaboration.data.network

import com.clockwise.features.collaboration.data.dto.PostDto
import com.clockwise.features.collaboration.data.dto.PostListResponse
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface RemoteCollaborationDataSource {
    suspend fun getPostsForBusinessUnit(
        businessUnitId: String,
        page: Int,
        size: Int
    ): Result<PostListResponse, DataError.Remote>
    
    suspend fun getPostById(postId: String): Result<PostDto, DataError.Remote>
}