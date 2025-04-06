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
import androidx.navigation.NavController
import com.clockwise.service.UserService
import com.clockwise.user.domain.UserRole
import com.clockwise.user.domain.AccessControl
import com.clockwise.user.presentation.home.profile.ProfileScreen
import com.clockwise.user.presentation.home.welcome.WelcomeScreen
import com.clockwise.user.presentation.home.schedule.WeeklyScheduleScreen
import com.clockwise.user.presentation.home.calendar.CalendarScreen
import com.clockwise.user.presentation.home.HomeAction
import com.clockwise.user.presentation.home.profile.ProfileViewModel
import com.clockwise.user.presentation.home.search.SearchScreen
import org.koin.compose.viewmodel.koinViewModel


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
    object Search : HomeScreen("search")
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel,
    onNavigate: (HomeScreen) -> Unit,
    navController: NavController? = null,
    userService: UserService
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val profileViewModel = koinViewModel<ProfileViewModel>()
    val profileState by profileViewModel.state.collectAsStateWithLifecycle()
    
    // Use the AccessControl utility to check if the user has access to the Search screen
    val showSearchTab = AccessControl.hasAccessToScreen("search", userService)
    
    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = White,
                contentColor = LightPurple,
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
                if (showSearchTab) {
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        label = { Text("Search") },
                        selected = state.currentScreen == HomeScreen.Search,
                        onClick = { 
                            viewModel.onAction(HomeAction.Navigate(HomeScreen.Search))
                            onNavigate(HomeScreen.Search)
                        }
                    )
                }
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
        Box(modifier = Modifier.padding(paddingValues)) {
            when (state.currentScreen) {
                HomeScreen.Welcome -> WelcomeScreen(
                    state = state.welcomeState,
                    onAction = { action ->
                        viewModel.onAction(HomeAction.WelcomeScreenAction(action))
                    }
                )
                HomeScreen.WeeklySchedule -> WeeklyScheduleScreen(
                    state = state.weeklyScheduleState,
                    onAction = { action ->
                        viewModel.onAction(HomeAction.WeeklyScheduleScreenAction(action))
                    }
                )
                HomeScreen.Calendar -> CalendarScreen(
                    state = state.calendarState,
                    onAction = { action ->
                        viewModel.onAction(HomeAction.CalendarScreenAction(action))
                    }
                )
                HomeScreen.Search -> {
                    if (AccessControl.hasAccessToScreen("search", userService)) {
                        SearchScreen(
                            state = state.searchState,
                            onAction = { action ->
                                viewModel.onAction(HomeAction.SearchScreenAction(action))
                            }
                        )
                    } else {
                        // Redirect to Welcome screen if user doesn't have permission
                        LaunchedEffect(Unit) {
                            viewModel.onAction(HomeAction.Navigate(HomeScreen.Welcome))
                            onNavigate(HomeScreen.Welcome)
                        }
                    }
                }
                HomeScreen.Profile -> ProfileScreen(
                    state = profileState,
                    onAction = { action ->
                        profileViewModel.onAction(action)
                    },
                    navController = navController
                )
            }
        }
    }
} 