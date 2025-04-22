package com.clockwise.features.welcome.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.clockwise.core.UserService
import com.clockwise.features.shift.schedule.presentation.getWeekStartDate

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
        onAction = { action -> 
            viewModel.onAction(action)
        },
        onNavigate = onNavigate,
        navController = navController,
        userService = userService
    )
} 