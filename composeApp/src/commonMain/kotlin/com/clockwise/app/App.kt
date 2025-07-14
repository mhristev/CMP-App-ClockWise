package com.clockwise.app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.clockwise.app.navigation.NavigationRoutes
import org.koin.compose.viewmodel.koinViewModel
import com.clockwise.features.auth.UserService
import org.koin.compose.koinInject
import com.clockwise.app.security.AccessControl

import com.clockwise.features.auth.presentation.AuthScreenRoot
import com.clockwise.features.auth.presentation.AuthViewModel
import com.clockwise.features.auth.presentation.SplashScreenRoot
import com.clockwise.features.auth.presentation.SplashViewModel
import com.clockwise.features.shift.presentation.welcome_shifts.WelcomeViewModel
import com.clockwise.features.shift.presentation.welcome_shifts.WelcomeScreenRoot
import com.clockwise.features.profile.presentation.ProfileScreenRoot
import com.clockwise.features.shift.presentation.week_schedule.WeeklyScheduleViewModel
import com.clockwise.features.shift.presentation.week_schedule.WeeklyScheduleScreenRoot
import com.clockwise.features.availability.presentation.calendar.CalendarViewModel
import com.clockwise.features.availability.presentation.calendar.CalendarScreenRoot
import com.clockwise.features.clockin.presentation.ClockInScreen
import com.clockwise.features.clockin.presentation.ClockInViewModel
import com.clockwise.features.location.domain.model.BusinessUnitAddress
import com.clockwise.features.business.presentation.BusinessViewModel
import com.clockwise.features.business.presentation.BusinessScreenRoot
import com.clockwise.features.business.presentation.add_employee.SearchViewModel
import com.clockwise.features.business.presentation.add_employee.SearchScreenRoot
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.clockwise.features.profile.presentation.ProfileViewModel

private val LightPurple = Color(0xFF4A2B8C)
private val White = Color(0xFFFFFFFF)

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val userService = koinInject<UserService>()
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        val currentUserRole by userService.currentUserRole.collectAsState()
        
        Scaffold(
            bottomBar = {
                // Only show bottom navigation if not on splash or auth screens
                if (currentRoute != NavigationRoutes.Auth.route && currentRoute != NavigationRoutes.Splash.route) {
                    BottomNavigation(
                        backgroundColor = White,
                        contentColor = LightPurple,
                        elevation = 8.dp
                    ) {
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "Welcome") },
                            label = { Text("Home") },
                            selected = currentRoute == NavigationRoutes.Welcome.route,
                            onClick = { navController.navigate(NavigationRoutes.Welcome.route) }
                        )
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.DateRange, contentDescription = "Weekly Schedule") },
                            label = { Text("Schedule") },
                            selected = currentRoute == NavigationRoutes.WeeklySchedule.route,
                            onClick = { navController.navigate(NavigationRoutes.WeeklySchedule.route) }
                        )
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar") },
                            label = { Text("Calendar") },
                            selected = currentRoute == NavigationRoutes.Calendar.route,
                            onClick = { navController.navigate(NavigationRoutes.Calendar.route) }
                        )
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Clock In") },
                            label = { Text("Clock In") },
                            selected = currentRoute == NavigationRoutes.ClockIn.route,
                            onClick = { navController.navigate(NavigationRoutes.ClockIn.route) }
                        )
                        if (AccessControl.hasAccessToScreen("business", currentUserRole)) {
                            BottomNavigationItem(
                                icon = { Icon(Icons.Default.Lock, contentDescription = "Business") },
                                label = { Text("Business") },
                                selected = currentRoute == NavigationRoutes.Business.route,
                                onClick = { navController.navigate(NavigationRoutes.Business.route) }
                            )
                        }
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile") },
                            selected = currentRoute == NavigationRoutes.Profile.route,
                            onClick = { navController.navigate(NavigationRoutes.Profile.route) }
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController, 
                startDestination = NavigationRoutes.Splash.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(NavigationRoutes.Auth.route) {
                    val viewModel = koinViewModel<AuthViewModel>()
                    AuthScreenRoot(
                        viewModel = viewModel,
                        navController = navController
                    )
                }
                
                composable(NavigationRoutes.Splash.route) {
                    val viewModel = koinViewModel<SplashViewModel>()
                    SplashScreenRoot(
                        viewModel = viewModel,
                        navController = navController
                    )
                }
                
                composable(NavigationRoutes.Welcome.route) {
                    val viewModel = koinViewModel<WelcomeViewModel>()
                    WelcomeScreenRoot(viewModel = viewModel)
                }
                
                composable(NavigationRoutes.WeeklySchedule.route) {
                    val viewModel = koinViewModel<WeeklyScheduleViewModel>()
                    WeeklyScheduleScreenRoot(viewModel = viewModel)
                }
                
                composable(NavigationRoutes.Calendar.route) {
                    val viewModel = koinViewModel<CalendarViewModel>()
                    CalendarScreenRoot(viewModel = viewModel)
                }
                
                composable(NavigationRoutes.ClockIn.route) {
                    val viewModel = koinViewModel<ClockInViewModel>()
                    val businessUnitAddress = BusinessUnitAddress(
                        businessUnitId = "1", 
                        name = "Vkushti",
                        address = "Leenderweg 255, Eindhoven, Netherlands",
                        latitude = 51.4209, // Accurate Leenderweg 255, Eindhoven coordinates
                        longitude = 5.4935,
                        allowedRadius = 250.0 // 100 meters
                    )
                    ClockInScreen(
                        businessUnitAddress = businessUnitAddress,
                        viewModel = viewModel
                    )
                }
                
                composable(NavigationRoutes.Profile.route) {
                    val viewModel = koinViewModel<ProfileViewModel>()
                    ProfileScreenRoot(
                        viewModel = viewModel,
                        navController = navController
                    )
                }
                
                composable(NavigationRoutes.Business.route) {
                    if (AccessControl.hasAccessToScreen("business", currentUserRole)) {
                        val viewModel = koinViewModel<BusinessViewModel>()
                        BusinessScreenRoot(
                            viewModel = viewModel,
                            onNavigateToSearch = {
                                navController.navigate(NavigationRoutes.Search.route)
                            }
                        )
                    } else {
                        LaunchedEffect(Unit) {
                            navController.navigate(NavigationRoutes.Welcome.route)
                        }
                    }
                }
                
                composable(NavigationRoutes.Search.route) {
                    if (AccessControl.hasAccessToScreen("search", currentUserRole)) {
                        val viewModel = koinViewModel<SearchViewModel>()
                        SearchScreenRoot(
                            viewModel = viewModel,
                            onNavigateBack = { 
                                navController.popBackStack()
                            }
                        )
                    } else {
                        LaunchedEffect(Unit) {
                            navController.navigate(NavigationRoutes.Welcome.route)
                        }
                    }
                }
            }
        }
    }
}