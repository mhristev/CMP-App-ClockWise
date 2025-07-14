package com.clockwise.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
// Temporarily commented out for step-by-step integration
// import com.clockwise.features.clockin.presentation.ClockInScreenRoot

/**
 * Example navigation setup including the clock-in feature.
 * This shows how to integrate the location-based clock-in screen.
 */
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            // Home screen composable
            // HomeScreenRoot()
        }
        
        // Temporarily commented out for step-by-step integration
        // composable("clockin") {
        //     ClockInScreenRoot()
        // }
        
        composable("profile") {
            // Profile screen composable
            // ProfileScreenRoot()
        }
        
        composable("availability") {
            // Availability screen composable
            // AvailabilityScreenRoot()
        }
        
        // Add other screens as needed
    }
}

/**
 * Navigation destinations for the app.
 */
object Destinations {
    const val HOME = "home"
    const val CLOCK_IN = "clockin"
    const val PROFILE = "profile"
    const val AVAILABILITY = "availability"
}
