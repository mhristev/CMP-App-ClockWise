package com.clockwise.features.shiftexchange.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShiftExchangeScreenRoot(
    viewModel: ShiftExchangeViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    
    ShiftExchangeScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}