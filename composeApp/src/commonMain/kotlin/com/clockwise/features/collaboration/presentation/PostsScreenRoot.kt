package com.clockwise.features.collaboration.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PostsScreenRoot(
    navController: NavController,
    onOpenDrawer: (() -> Unit)? = null,
    viewModel: PostsViewModel = koinViewModel()
) {
    PostsScreen(
        onNavigateBack = { navController.popBackStack() },
        onOpenDrawer = onOpenDrawer,
        viewModel = viewModel
    )
}