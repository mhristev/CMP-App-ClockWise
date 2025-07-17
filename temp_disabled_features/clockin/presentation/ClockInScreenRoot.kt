package com.clockwise.features.clockin.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import com.clockwise.features.location.domain.model.BusinessUnitAddress

/**
 * Root composable for the clock-in screen that sets up the ViewModel.
 */
@Composable
fun ClockInScreenRoot(
    modifier: Modifier = Modifier
) {
    val viewModel: ClockInViewModel = koinViewModel()
    
    // Use the same business unit address that's hardcoded in the ViewModel
    val businessUnitAddress = BusinessUnitAddress(
        businessUnitId = "1", 
        name = "Main Office",
        address = "123 Business St, Boston, MA",
        latitude = 42.3601, // Boston coordinates
        longitude = -71.0589,
        allowedRadius = 100.0 // 100 meters
    )
    
    ClockInScreen(
        businessUnitAddress = businessUnitAddress,
        viewModel = viewModel
    )
}
