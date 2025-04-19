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
import com.clockwise.user.presentation.home.business.BusinessScreen
import com.clockwise.user.presentation.home.business.BusinessViewModel
import com.clockwise.user.presentation.home.business.BusinessView
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
    val profileViewModel = koinViewModel<ProfileViewModel>()
    val profileState by profileViewModel.state.collectAsStateWithLifecycle()
    
    // Use the AccessControl utility to check if the user has access to the Search screen
    val showSearchTab = AccessControl.hasAccessToScreen("search", userService)
    
    // Use the AccessControl utility to check if the user has access to the Business screen
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
                HomeScreen.Welcome -> WelcomeScreen(
                    state = state.welcomeState,
                    onAction = { action ->
                        onAction(HomeAction.WelcomeScreenAction(action))
                    }
                )
                HomeScreen.WeeklySchedule -> WeeklyScheduleScreen(
                    state = state.weeklyScheduleState,
                    onAction = { action ->
                        onAction(HomeAction.WeeklyScheduleScreenAction(action))
                    }
                )
                HomeScreen.Calendar -> CalendarScreen(
                    state = state.calendarState,
                    onAction = { action ->
                        onAction(HomeAction.CalendarScreenAction(action))
                    }
                )
                HomeScreen.Search -> {
                    if (AccessControl.hasAccessToScreen("search", userService)) {
                        SearchScreen(
                            state = state.searchState,
                            onAction = { action ->
                                onAction(HomeAction.SearchScreenAction(action))
                            },
                            onNavigateBack = { 
                                // Navigate back to the previous screen or Business screen
                                onAction(HomeAction.NavigateToScreen(previousScreen ?: HomeScreen.Business))
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
                        BusinessScreen(
                            state = state.businessState,
                            onAction = { action ->
                                onAction(HomeAction.BusinessScreenAction(action))
                            },
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