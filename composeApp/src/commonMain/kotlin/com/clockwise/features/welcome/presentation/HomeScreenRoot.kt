package com.clockwise.features.welcome.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.clockwise.core.UserService

@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel,
    onNavigate: (HomeScreen) -> Unit,
    navController: NavController? = null,
    userService: UserService
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreenContent(
        state = state,
        onAction = viewModel::onAction,
        onNavigate = onNavigate,
        navController = navController,
        userService = userService
    )
} 