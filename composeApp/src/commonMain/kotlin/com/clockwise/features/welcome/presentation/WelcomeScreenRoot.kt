package com.clockwise.features.welcome.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WelcomeScreenRoot(
    viewModel: WelcomeViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    WelcomeScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
} 