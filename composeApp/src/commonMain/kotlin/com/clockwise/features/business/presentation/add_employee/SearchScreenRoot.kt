package com.clockwise.features.business.presentation.add_employee

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SearchScreenRoot(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SearchScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        },
        onNavigateBack = onNavigateBack
    )
} 