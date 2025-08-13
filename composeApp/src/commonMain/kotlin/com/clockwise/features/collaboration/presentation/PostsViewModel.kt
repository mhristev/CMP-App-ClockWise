package com.clockwise.features.collaboration.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockwise.features.collaboration.domain.usecase.GetPostByIdUseCase
import com.clockwise.features.collaboration.domain.usecase.GetPostsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostsViewModel(
    private val getPostsUseCase: GetPostsUseCase,
    private val getPostByIdUseCase: GetPostByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PostsState())
    val state = _state.asStateFlow()

    init {
        onAction(PostsAction.LoadPosts)
    }

    fun onAction(action: PostsAction) {
        when (action) {
            PostsAction.LoadPosts -> loadPosts()
            PostsAction.RefreshPosts -> refreshPosts()
            PostsAction.LoadMorePosts -> loadMorePosts()
            is PostsAction.SelectPost -> selectPost(action.postId)
            PostsAction.DismissPostDetail -> dismissPostDetail()
            PostsAction.ClearError -> clearError()
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            getPostsUseCase(page = 0, size = 20)
                .onStart {
                    _state.update {
                        it.copy(
                            isLoading = true,
                            error = null,
                            currentPage = 0
                        )
                    }
                }
                .catch { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load posts"
                        )
                    }
                }
                .collect { posts ->
                    _state.update {
                        it.copy(
                            posts = posts,
                            isLoading = false,
                            error = null,
                            hasMorePages = posts.size >= 20
                        )
                    }
                }
        }
    }

    private fun refreshPosts() {
        viewModelScope.launch {
            getPostsUseCase(page = 0, size = 20)
                .onStart {
                    _state.update {
                        it.copy(
                            isRefreshing = true,
                            error = null
                        )
                    }
                }
                .catch { error ->
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            error = error.message ?: "Failed to refresh posts"
                        )
                    }
                }
                .collect { posts ->
                    _state.update {
                        it.copy(
                            posts = posts,
                            isRefreshing = false,
                            error = null,
                            currentPage = 0,
                            hasMorePages = posts.size >= 20
                        )
                    }
                }
        }
    }

    private fun loadMorePosts() {
        if (_state.value.isLoadingMore || !_state.value.hasMorePages) return

        viewModelScope.launch {
            val nextPage = _state.value.currentPage + 1
            
            getPostsUseCase(page = nextPage, size = 20)
                .onStart {
                    _state.update {
                        it.copy(isLoadingMore = true)
                    }
                }
                .catch { error ->
                    _state.update {
                        it.copy(
                            isLoadingMore = false,
                            error = error.message ?: "Failed to load more posts"
                        )
                    }
                }
                .collect { newPosts ->
                    _state.update {
                        it.copy(
                            posts = it.posts + newPosts,
                            isLoadingMore = false,
                            currentPage = nextPage,
                            hasMorePages = newPosts.size >= 20
                        )
                    }
                }
        }
    }

    private fun selectPost(postId: String) {
        viewModelScope.launch {
            try {
                val post = getPostByIdUseCase(postId)
                _state.update {
                    it.copy(
                        selectedPost = post,
                        showPostDetail = post != null
                    )
                }
            } catch (error: Exception) {
                _state.update {
                    it.copy(
                        error = error.message ?: "Failed to load post details"
                    )
                }
            }
        }
    }

    private fun dismissPostDetail() {
        _state.update {
            it.copy(
                selectedPost = null,
                showPostDetail = false
            )
        }
    }

    private fun clearError() {
        _state.update {
            it.copy(error = null)
        }
    }
}