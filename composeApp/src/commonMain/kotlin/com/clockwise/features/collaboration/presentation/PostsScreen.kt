@file:OptIn(ExperimentalMaterialApi::class)

package com.clockwise.features.collaboration.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.clockwise.features.collaboration.presentation.components.PostCard
import com.clockwise.features.collaboration.presentation.components.PostDetailModal
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PostsScreen(
    onNavigateBack: () -> Unit,
    viewModel: PostsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(error)
                viewModel.onAction(PostsAction.ClearError)
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Posts",
                        style = MaterialTheme.typography.h5
                    ) 
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                elevation = 8.dp
            )
        }
    ) { paddingValues ->
        PostsContent(
            state = state,
            onAction = viewModel::onAction,
            paddingValues = paddingValues
        )
    }
    
    state.selectedPost?.let { selectedPost ->
        if (state.showPostDetail) {
            PostDetailModal(
                post = selectedPost,
                onDismiss = { viewModel.onAction(PostsAction.DismissPostDetail) }
            )
        }
    }
}

@Composable
private fun PostsContent(
    state: PostsState,
    onAction: (PostsAction) -> Unit,
    paddingValues: PaddingValues
) {
    // Pull to refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading && state.posts.isNotEmpty(),
        onRefresh = {
            onAction(PostsAction.RefreshPosts)
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .pullRefresh(pullRefreshState)
    ) {
        when {
            state.isLoading && state.posts.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            state.posts.isEmpty() && !state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "No posts available",
                            style = MaterialTheme.typography.body1,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            else -> {
                val listState = rememberLazyListState()
                
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(
                        items = state.posts,
                        key = { it.id }
                    ) { post ->
                        PostCard(
                            post = post,
                            onClick = { onAction(PostsAction.SelectPost(post.id)) },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
                
                val shouldLoadMore by remember {
                    derivedStateOf {
                        val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                        lastVisibleIndex >= state.posts.size - 5 && 
                        state.hasMorePages && 
                        !state.isLoadingMore
                    }
                }
                
                LaunchedEffect(shouldLoadMore) {
                    if (shouldLoadMore) {
                        onAction(PostsAction.LoadMorePosts)
                    }
                }
            }
        }

        // Pull to refresh indicator
        PullRefreshIndicator(
            refreshing = state.isLoading && state.posts.isNotEmpty(),
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color.White,
            contentColor = MaterialTheme.colors.primary
        )
    }
}