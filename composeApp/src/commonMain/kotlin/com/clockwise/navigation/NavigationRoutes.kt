package com.clockwise.navigation

/**
 * Sealed class representing all navigation routes in the application.
 * This provides type safety and prevents typos in route names.
 */
sealed class NavigationRoutes(val route: String) {
    object Auth : NavigationRoutes("auth")
    object Register : NavigationRoutes("register")
    object Home : NavigationRoutes("home")
    
    // Add more routes as needed
    
    companion object {
        // Helper function to get route by name (for backward compatibility)
        fun fromRoute(route: String): NavigationRoutes {
            return when (route) {
                Auth.route -> Auth
                Register.route -> Register
                Home.route -> Home
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
        }
    }
} 