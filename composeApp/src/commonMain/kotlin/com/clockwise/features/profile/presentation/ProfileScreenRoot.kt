package com.clockwise.features.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.clockwise.app.navigation.NavigationRoutes

@Composable
fun ProfileScreenRoot(
    viewModel: ProfileViewModel,
    navController: NavController? = null
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Observe the redirectToAuth flag and navigate when it's true
    LaunchedEffect(state.redirectToAuth) {
        if (state.redirectToAuth && navController != null) {
            navController.navigate(NavigationRoutes.Auth.route) {
                popUpTo(0) // Clear entire back stack
                launchSingleTop = true
            }
        }
    }

    ProfileScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        },
        navController = navController,
        viewModel = viewModel
    )
} 