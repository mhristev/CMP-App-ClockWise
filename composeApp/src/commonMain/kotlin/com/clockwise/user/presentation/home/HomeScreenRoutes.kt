package com.clockwise.user.presentation.home

/**
 * Sealed class representing the different screens in the home section of the app.
 * Each screen has a unique route identifier.
 */
sealed class HomeScreen(val route: String) {
    object Welcome : HomeScreen("welcome")
    object WeeklySchedule : HomeScreen("weekly_schedule")
    object Calendar : HomeScreen("calendar")
    object Profile : HomeScreen("profile")
    object Search : HomeScreen("search")
} 