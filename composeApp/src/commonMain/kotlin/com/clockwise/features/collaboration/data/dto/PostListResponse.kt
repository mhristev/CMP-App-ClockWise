package com.clockwise.features.collaboration.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostListResponse(
    val posts: List<PostDto>,
    val page: Int,
    val size: Int,
    val total: Long
)