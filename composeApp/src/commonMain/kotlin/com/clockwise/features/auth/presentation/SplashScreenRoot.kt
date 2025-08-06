package com.clockwise.features.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.clockwise.app.navigation.NavigationRoutes

@Composable
fun SplashScreenRoot(
    viewModel: SplashViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()

    // Handle navigation when initialization is complete
    LaunchedEffect(state.initializationComplete) {
        if (state.initializationComplete) {
            if (state.isAuthenticated && state.hasBusinessUnit) {
                // User is authenticated and has business unit - go to main app
                navController.navigate(NavigationRoutes.BusinessUnitLanding.route) {
                    popUpTo(NavigationRoutes.Splash.route) {
                        inclusive = true
                    }
                }
            } else {
                // User needs to login or has no business unit - go to auth
                navController.navigate(NavigationRoutes.Auth.route) {
                    popUpTo(NavigationRoutes.Splash.route) {
                        inclusive = true
                    }
                }
            }
        }
    }

    SplashScreen(
        isRefreshingToken = state.isRefreshingToken,
        onSplashComplete = {
            // This callback is now handled by the LaunchedEffect above
            // to ensure proper navigation timing
        }
    )
} 