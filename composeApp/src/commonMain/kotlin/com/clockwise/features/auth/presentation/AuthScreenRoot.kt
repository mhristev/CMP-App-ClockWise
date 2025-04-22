package com.clockwise.features.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.clockwise.app.navigation.NavigationRoutes

@Composable
fun AuthScreenRoot(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isAuthenticated, state.hasBusinessUnit) {
        if (state.isAuthenticated && state.hasBusinessUnit) {
            navController.navigate(NavigationRoutes.Welcome.route) {
                popUpTo(NavigationRoutes.Auth.route)
            }
        }
    }

    AuthScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
} 