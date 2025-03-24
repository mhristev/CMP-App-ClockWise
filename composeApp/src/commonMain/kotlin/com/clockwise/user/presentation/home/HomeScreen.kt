package com.clockwise.user.presentation.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.arkivanov.decompose.router.stack.StackNavigation
//import com.arkivanov.decompose.value.Value
//import com.arkivanov.essenty.lifecycle.doOnCreate
import com.clockwise.user.presentation.home.profile.ProfileScreen
import com.clockwise.user.presentation.home.welcome.WelcomeScreen
import com.clockwise.user.presentation.home.schedule.WeeklyScheduleScreen
import com.clockwise.user.presentation.home.calendar.CalendarScreen
import com.clockwise.user.presentation.home.HomeAction


private val LightPurple = Color(0xFF4A2B8C)
private val LightPurpleVariant = Color(0xFF6B4BAE)
private val White = Color(0xFFFFFFFF)
private val LightGray = Color(0xFFF5F5F5)
private val DarkGray = Color(0xFF333333)

sealed class HomeScreen(val route: String) {
    object Welcome : HomeScreen("welcome")
    object WeeklySchedule : HomeScreen("weekly_schedule")
    object Calendar : HomeScreen("calendar")
    object Profile : HomeScreen("profile")
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel,
    onNavigate: (HomeScreen) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = White,
                contentColor = LightPurple,
//                selectedItemColor = LightPurpleVariant,
//                unselectedItemColor = LightPurple,
                elevation = 8.dp
            ) {
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Welcome") },
                    label = { Text("Home") },
                    selected = state.currentScreen == HomeScreen.Welcome,
                    onClick = { 
                        viewModel.onAction(HomeAction.Navigate(HomeScreen.Welcome))
                        onNavigate(HomeScreen.Welcome)
                    }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Weekly Schedule") },
                    label = { Text("Schedule") },
                    selected = state.currentScreen == HomeScreen.WeeklySchedule,
                    onClick = { 
                        viewModel.onAction(HomeAction.Navigate(HomeScreen.WeeklySchedule))
                        onNavigate(HomeScreen.WeeklySchedule)
                    }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar") },
                    label = { Text("Calendar") },
                    selected = state.currentScreen == HomeScreen.Calendar,
                    onClick = { 
                        viewModel.onAction(HomeAction.Navigate(HomeScreen.Calendar))
                        onNavigate(HomeScreen.Calendar)
                    }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = state.currentScreen == HomeScreen.Profile,
                    onClick = { 
                        viewModel.onAction(HomeAction.Navigate(HomeScreen.Profile))
                        onNavigate(HomeScreen.Profile)
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LightGray)
        ) {
            AnimatedContent(
                targetState = state.currentScreen,
                transitionSpec = {
                    fadeIn() + slideInHorizontally() with 
                    fadeOut() + slideOutHorizontally()
                }
            ) { screen ->
                when (screen) {
                    HomeScreen.Welcome -> WelcomeScreen(
                        state = state.welcomeState,
                        onAction = { viewModel.onAction(HomeAction.WelcomeScreenAction(it)) }
                    )
                    HomeScreen.WeeklySchedule -> WeeklyScheduleScreen(
                        state = state.weeklyScheduleState,
                        onAction = { viewModel.onAction(HomeAction.WeeklyScheduleScreenAction(it)) }
                    )
                    HomeScreen.Calendar -> CalendarScreen(
                        state = state.calendarState,
                        onAction = { viewModel.onAction(HomeAction.CalendarScreenAction(it)) }
                    )
                    HomeScreen.Profile -> ProfileScreen(
                        state = state.profileState,
                        onAction = { viewModel.onAction(HomeAction.ProfileScreenAction(it)) }
                    )
                }
            }
        }
    }
} 