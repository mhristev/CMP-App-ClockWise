package com.clockwise.features.collaboration.presentation

import com.clockwise.features.collaboration.domain.model.Post

data class PostsState(
    val posts: List<Post> = emptyList(),
    val selectedPost: Post? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true,
    val showPostDetail: Boolean = false,
    val highlightedPostIds: Set<String> = emptySet(),
    val notificationBadgeCount: Int = 0
)