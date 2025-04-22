package com.clockwise.features.welcome.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.clockwise.core.UserService
import com.clockwise.app.security.AccessControl
import com.clockwise.features.profile.presentation.ProfileScreenRoot
import com.clockwise.features.profile.presentation.ProfileViewModel
import com.clockwise.features.shift.schedule.presentation.WeeklyScheduleScreenRoot
import com.clockwise.features.shift.schedule.presentation.WeeklyScheduleViewModel
import com.clockwise.features.availability.calendar.presentation.CalendarScreenRoot
import com.clockwise.features.availability.calendar.presentation.CalendarViewModel
import com.clockwise.features.business.presentation.add_employee.SearchScreenRoot
import com.clockwise.features.business.presentation.add_employee.SearchViewModel
import com.clockwise.features.business.presentation.BusinessScreenRoot
import com.clockwise.features.business.presentation.BusinessViewModel
import org.koin.compose.viewmodel.koinViewModel


private val LightPurple = Color(0xFF4A2B8C)
private val LightPurpleVariant = Color(0xFF6B4BAE)
private val White = Color(0xFFFFFFFF)
private val LightGray = Color(0xFFF5F5F5)
private val DarkGray = Color(0xFF333333)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreenContent(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    onNavigate: (HomeScreen) -> Unit,
    navController: NavController? = null,
    userService: UserService
) {
    // Use the AccessControl utility to check if the user has access to screens
    val showSearchTab = AccessControl.hasAccessToScreen("search", userService)
    val showBusinessTab = AccessControl.hasAccessToScreen("business", userService)
    
    // Track the previous screen for back navigation
    var previousScreen by remember { mutableStateOf<HomeScreen?>(null) }
    
    // Update previous screen when current screen changes
    LaunchedEffect(state.currentScreen) {
        if (state.currentScreen != HomeScreen.Search) {
            previousScreen = state.currentScreen
        }
    }
    
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
                        onAction(HomeAction.NavigateToScreen(HomeScreen.Welcome))
                    }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Weekly Schedule") },
                    label = { Text("Schedule") },
                    selected = state.currentScreen == HomeScreen.WeeklySchedule,
                    onClick = { 
                        onAction(HomeAction.NavigateToScreen(HomeScreen.WeeklySchedule))
                    }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar") },
                    label = { Text("Calendar") },
                    selected = state.currentScreen == HomeScreen.Calendar,
                    onClick = { 
                        onAction(HomeAction.NavigateToScreen(HomeScreen.Calendar))
                    }
                )
                if (showBusinessTab) {
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.Lock, contentDescription = "Business") },
                        label = { Text("Business") },
                        selected = state.currentScreen == HomeScreen.Business,
                        onClick = { 
                            onAction(HomeAction.NavigateToScreen(HomeScreen.Business))
                        }
                    )
                }
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = state.currentScreen == HomeScreen.Profile,
                    onClick = { 
                        onAction(HomeAction.NavigateToScreen(HomeScreen.Profile))
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (state.currentScreen) {
                HomeScreen.Welcome -> {
                    val welcomeViewModel = koinViewModel<WelcomeViewModel>()
                    WelcomeScreenRoot(viewModel = welcomeViewModel)
                }
                HomeScreen.WeeklySchedule -> {
                    val weeklyScheduleViewModel = koinViewModel<WeeklyScheduleViewModel>()
                    WeeklyScheduleScreenRoot(viewModel = weeklyScheduleViewModel)
                }
                HomeScreen.Calendar -> {
                    val calendarViewModel = koinViewModel<CalendarViewModel>()
                    CalendarScreenRoot(viewModel = calendarViewModel)
                }
                HomeScreen.Search -> {
                    if (AccessControl.hasAccessToScreen("search", userService)) {
                        val searchViewModel = koinViewModel<SearchViewModel>()
                        SearchScreenRoot(
                            viewModel = searchViewModel,
                            onNavigateBack = { 
                                // Navigate back to the previous screen or Business screen
                                onAction(
                                    HomeAction.NavigateToScreen(
                                        previousScreen ?: HomeScreen.Business
                                    )
                                )
                            }
                        )
                    } else {
                        // Redirect to Welcome screen if user doesn't have permission
                        LaunchedEffect(Unit) {
                            onAction(HomeAction.NavigateToScreen(HomeScreen.Welcome))
                        }
                    }
                }
                HomeScreen.Business -> {
                    if (AccessControl.hasAccessToScreen("business", userService)) {
                        val businessViewModel = koinViewModel<BusinessViewModel>()
                        BusinessScreenRoot(
                            viewModel = businessViewModel,
                            onNavigateToSearch = {
                                onAction(HomeAction.NavigateToScreen(HomeScreen.Search))
                            }
                        )
                    } else {
                        // Redirect to Welcome screen if user doesn't have permission
                        LaunchedEffect(Unit) {
                            onAction(HomeAction.NavigateToScreen(HomeScreen.Welcome))
                        }
                    }
                }
                HomeScreen.Profile -> {
                    val profileViewModel = koinViewModel<ProfileViewModel>()
                    ProfileScreenRoot(
                        viewModel = profileViewModel,
                        navController = navController
                    )
                }
            }
        }
    }
} 