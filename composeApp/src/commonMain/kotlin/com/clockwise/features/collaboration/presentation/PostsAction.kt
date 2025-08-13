package com.clockwise.features.collaboration.presentation

sealed interface PostsAction {
    data object LoadPosts : PostsAction
    data object RefreshPosts : PostsAction
    data object LoadMorePosts : PostsAction
    data class SelectPost(val postId: String) : PostsAction
    data object DismissPostDetail : PostsAction
    data object ClearError : PostsAction
}