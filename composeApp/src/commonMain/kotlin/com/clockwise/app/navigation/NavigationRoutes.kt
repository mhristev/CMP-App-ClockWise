package com.clockwise.app.navigation

/**
 * Sealed class representing all navigation routes in the application.
 * This provides type safety and prevents typos in route names.
 */
sealed class NavigationRoutes(val route: String) {
    object Splash : NavigationRoutes("splash")
    object Auth : NavigationRoutes("auth")
    // Business Unit Landing as the new default home screen
    object BusinessUnitLanding : NavigationRoutes("businessunit_landing")
    // Other screens accessible through drawer menu
    object Welcome : NavigationRoutes("welcome")
    object WeeklySchedule : NavigationRoutes("weekly_schedule")
    object Calendar : NavigationRoutes("calendar")
    object ClockIn : NavigationRoutes("clock_in")
    object Profile : NavigationRoutes("profile")
    object Search : NavigationRoutes("search")
    object Business : NavigationRoutes("business")
    object Settings : NavigationRoutes("settings")
    object Notifications : NavigationRoutes("notifications")
    
    companion object {
        // Helper function to get route by name (for backward compatibility)
        fun fromRoute(route: String): NavigationRoutes {
            return when (route) {
                Splash.route -> Splash
                Auth.route -> Auth
                BusinessUnitLanding.route -> BusinessUnitLanding
                Welcome.route -> Welcome
                WeeklySchedule.route -> WeeklySchedule
                Calendar.route -> Calendar
                ClockIn.route -> ClockIn
                Profile.route -> Profile
                Search.route -> Search
                Business.route -> Business
                Settings.route -> Settings
                Notifications.route -> Notifications
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
        }
    }
} 