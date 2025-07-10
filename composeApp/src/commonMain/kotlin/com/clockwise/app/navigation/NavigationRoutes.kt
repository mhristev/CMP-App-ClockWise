package com.clockwise.app.navigation

/**
 * Sealed class representing all navigation routes in the application.
 * This provides type safety and prevents typos in route names.
 */
sealed class NavigationRoutes(val route: String) {
    object Splash : NavigationRoutes("splash")
    object Auth : NavigationRoutes("auth")
    // Instead of just "Home", we'll have all the screens as top-level routes
    object Welcome : NavigationRoutes("welcome")
    object WeeklySchedule : NavigationRoutes("weekly_schedule")
    object Calendar : NavigationRoutes("calendar")
    object Profile : NavigationRoutes("profile")
    object Search : NavigationRoutes("search")
    object Business : NavigationRoutes("business")
    
    companion object {
        // Helper function to get route by name (for backward compatibility)
        fun fromRoute(route: String): NavigationRoutes {
            return when (route) {
                Splash.route -> Splash
                Auth.route -> Auth
                Welcome.route -> Welcome
                WeeklySchedule.route -> WeeklySchedule
                Calendar.route -> Calendar
                Profile.route -> Profile
                Search.route -> Search
                Business.route -> Business
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
        }
    }
} 