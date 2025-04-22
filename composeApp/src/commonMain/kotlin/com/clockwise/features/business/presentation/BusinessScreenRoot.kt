package com.clockwise.features.business.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BusinessScreenRoot(
    viewModel: BusinessViewModel,
    onNavigateToSearch: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BusinessScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        },
        onNavigateToSearch = onNavigateToSearch
    )
} 