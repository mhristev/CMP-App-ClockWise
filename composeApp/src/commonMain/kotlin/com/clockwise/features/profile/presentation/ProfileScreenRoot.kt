package com.clockwise.features.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun ProfileScreenRoot(
    viewModel: ProfileViewModel,
    navController: NavController? = null
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProfileScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        },
        navController = navController
    )
} 