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
import com.clockwise.features.business.presentation.BusinessViewModel
import com.clockwise.features.business.presentation.BusinessScreenRoot
import com.clockwise.features.business.presentation.add_employee.SearchViewModel
import com.clockwise.features.business.presentation.add_employee.SearchScreenRoot
import com.clockwise.features.shiftexchange.presentation.ShiftExchangeViewModel
import com.clockwise.features.shiftexchange.presentation.ShiftExchangeScreenRoot
import com.clockwise.features.collaboration.presentation.PostsViewModel
import com.clockwise.features.collaboration.presentation.PostsScreenRoot
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.clockwise.features.profile.presentation.ProfileViewModel
import com.clockwise.features.sidemenu.presentation.SideMenuViewModel
import com.clockwise.features.sidemenu.presentation.SideMenuAction
import com.clockwise.features.sidemenu.presentation.BusinessUnitLandingScreen
import com.clockwise.features.sidemenu.presentation.components.DrawerContent
import com.clockwise.app.navigation.BottomNavigationBar

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val userService = koinInject<UserService>()
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        val currentUser by userService.currentUser.collectAsState()
        val currentUserRole by userService.currentUserRole.collectAsState()
        val sideMenuViewModel = koinViewModel<SideMenuViewModel>()
        val sideMenuState by sideMenuViewModel.state.collectAsState()
        
        var isDrawerOpen by remember { mutableStateOf(false) }
        val drawerWidth = 280.dp
        val density = LocalDensity.current
        
        // Animate drawer slide from RIGHT to LEFT
        val drawerOffset by animateFloatAsState(
            targetValue = if (isDrawerOpen) 0f else with(density) { drawerWidth.toPx() },
            animationSpec = tween(300)
        )
        
        // Functions to control drawer
        val openDrawer = { isDrawerOpen = true }
        val closeDrawer = { isDrawerOpen = false }
        
        // Check if we should show the drawer (not on auth/splash screens)
        val shouldShowDrawer = currentRoute != NavigationRoutes.Auth.route && 
                              currentRoute != NavigationRoutes.Splash.route
        
        // Single NavHost for all screens
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content with bottom padding when showing bottom navigation
            NavHost(
                navController = navController, 
                startDestination = NavigationRoutes.Splash.route,
                modifier = if (shouldShowDrawer) 
                    Modifier.fillMaxSize().padding(bottom = 88.dp) 
                else 
                    Modifier.fillMaxSize()
            ) {
                // Auth and splash screens (no drawer)
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
                
                // Main app screens (with drawer when authenticated)
                composable(NavigationRoutes.BusinessUnitLanding.route) {
                    // Ensure business unit data is loaded
                    LaunchedEffect(Unit) {
                        sideMenuViewModel.ensureBusinessUnitLoaded()
                    }
                    
                    if (shouldShowDrawer) {
                        BusinessUnitLandingScreen(
                            state = sideMenuState,
                            onAction = sideMenuViewModel::onAction,
                            onOpenDrawer = openDrawer
                        )
                    } else {
                        BusinessUnitLandingScreen(
                            state = sideMenuState,
                            onAction = sideMenuViewModel::onAction,
                            onOpenDrawer = { /* No drawer for auth screens */ }
                        )
                    }
                }
                
                composable(NavigationRoutes.Welcome.route) {
                    val viewModel = koinViewModel<WelcomeViewModel>()
                    if (shouldShowDrawer) {
                        WelcomeScreenWithDrawer(
                            viewModel = viewModel,
                            onOpenDrawer = openDrawer
                        )
                    } else {
                        WelcomeScreenRoot(viewModel = viewModel)
                    }
                }
                
                composable(NavigationRoutes.WeeklySchedule.route) {
                    val viewModel = koinViewModel<WeeklyScheduleViewModel>()
                    if (shouldShowDrawer) {
                        WeeklyScheduleScreenWithDrawer(
                            viewModel = viewModel,
                            onOpenDrawer = openDrawer
                        )
                    } else {
                        WeeklyScheduleScreenRoot(viewModel = viewModel)
                    }
                }
                
                composable(NavigationRoutes.Calendar.route) {
                    val viewModel = koinViewModel<CalendarViewModel>()
                    if (shouldShowDrawer) {
                        CalendarScreenWithDrawer(
                            viewModel = viewModel,
                            onOpenDrawer = openDrawer
                        )
                    } else {
                        CalendarScreenRoot(viewModel = viewModel)
                    }
                }
                
                composable(NavigationRoutes.ClockIn.route) {
                    LaunchedEffect(Unit) {
                        navController.navigate(NavigationRoutes.Welcome.route) {
                            popUpTo(NavigationRoutes.ClockIn.route) { inclusive = true }
                        }
                    }
                }
                
                composable(NavigationRoutes.Profile.route) {
                    val viewModel = koinViewModel<ProfileViewModel>()
                    if (shouldShowDrawer) {
                        ProfileScreenWithDrawer(
                            viewModel = viewModel,
                            navController = navController,
                            onOpenDrawer = openDrawer
                        )
                    } else {
                        ProfileScreenRoot(
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }
                
                composable(NavigationRoutes.ShiftExchange.route) {
                    val viewModel = koinViewModel<ShiftExchangeViewModel>()
                    if (shouldShowDrawer) {
                        ShiftExchangeScreenWithDrawer(
                            viewModel = viewModel,
                            onOpenDrawer = openDrawer
                        )
                    } else {
                        ShiftExchangeScreenRoot(viewModel = viewModel)
                    }
                }
                
                composable(NavigationRoutes.Posts.route) {
                    val viewModel = koinViewModel<PostsViewModel>()
                    if (shouldShowDrawer) {
                        PostsScreenWithDrawer(
                            viewModel = viewModel,
                            navController = navController,
                            onOpenDrawer = openDrawer
                        )
                    } else {
                        PostsScreenRoot(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
                
                composable(NavigationRoutes.Business.route) {
                    if (AccessControl.hasAccessToScreen("business", currentUserRole)) {
                        val viewModel = koinViewModel<BusinessViewModel>()
                        if (shouldShowDrawer) {
                            BusinessScreenWithDrawer(
                                viewModel = viewModel,
                                onNavigateToSearch = {
                                    navController.navigate(NavigationRoutes.Search.route)
                                },
                                onOpenDrawer = openDrawer
                            )
                        } else {
                            BusinessScreenRoot(
                                viewModel = viewModel,
                                onNavigateToSearch = {
                                    navController.navigate(NavigationRoutes.Search.route)
                                }
                            )
                        }
                    } else {
                        LaunchedEffect(Unit) {
                            navController.navigate(NavigationRoutes.BusinessUnitLanding.route)
                        }
                    }
                }
                
                composable(NavigationRoutes.Search.route) {
                    if (AccessControl.hasAccessToScreen("search", currentUserRole)) {
                        val viewModel = koinViewModel<SearchViewModel>()
                        if (shouldShowDrawer) {
                            SearchScreenWithDrawer(
                                viewModel = viewModel,
                                onNavigateBack = { 
                                    navController.popBackStack()
                                },
                                onOpenDrawer = openDrawer
                            )
                        } else {
                            SearchScreenRoot(
                                viewModel = viewModel,
                                onNavigateBack = { 
                                    navController.popBackStack()
                                }
                            )
                        }
                    } else {
                        LaunchedEffect(Unit) {
                            navController.navigate(NavigationRoutes.BusinessUnitLanding.route)
                        }
                    }
                }
                
                composable(NavigationRoutes.Settings.route) {
                    LaunchedEffect(Unit) {
                        navController.navigate(NavigationRoutes.Profile.route) {
                            popUpTo(NavigationRoutes.Settings.route) { inclusive = true }
                        }
                    }
                }
                
                composable(NavigationRoutes.Notifications.route) {
                    if (shouldShowDrawer) {
                        NotificationsScreenWithDrawer(onOpenDrawer = openDrawer)
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Notifications",
                                    style = MaterialTheme.typography.h5,
                                    color = MaterialTheme.colors.onSurface
                                )
                                Text(
                                    text = "Coming soon...",
                                    style = MaterialTheme.typography.body1,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            
            // Only show drawer overlay and content when shouldShowDrawer is true
            if (shouldShowDrawer) {
                // Overlay when drawer is open
                if (isDrawerOpen) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.3f))
                            .clickable { closeDrawer() }
                            .zIndex(1f)
                    )
                }
                
                // Drawer content positioned on the right side
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(drawerWidth)
                        .align(Alignment.TopEnd)
                        .graphicsLayer {
                            translationX = drawerOffset
                        }
                        .zIndex(2f)
                ) {
                    DrawerContent(
                        currentUser = currentUser,
                        currentRoute = currentRoute,
                        onNavigateToHome = {
                            closeDrawer()
                            navController.navigate(NavigationRoutes.BusinessUnitLanding.route) {
                                popUpTo(NavigationRoutes.BusinessUnitLanding.route) { inclusive = true }
                            }
                        },
                        onNavigateToProfile = {
                            closeDrawer()
                            navController.navigate(NavigationRoutes.Profile.route)
                        },
                        onNavigateToSchedule = {
                            closeDrawer()
                            navController.navigate(NavigationRoutes.WeeklySchedule.route)
                        },
                        onNavigateToCalendar = {
                            closeDrawer()
                            navController.navigate(NavigationRoutes.Calendar.route)
                        },
                        onNavigateToClockIn = {
                            closeDrawer()
                            navController.navigate(NavigationRoutes.ClockIn.route)
                        },
                        onNavigateToBusinessUnit = {
                            closeDrawer()
                            navController.navigate(NavigationRoutes.Business.route)
                        },
                        onNavigateToShiftExchange = {
                            closeDrawer()
                            navController.navigate(NavigationRoutes.ShiftExchange.route)
                        },
                        onNavigateToPosts = {
                            closeDrawer()
                            navController.navigate(NavigationRoutes.Posts.route)
                        },
                        onNavigateToSettings = {
                            closeDrawer()
                            navController.navigate(NavigationRoutes.Settings.route)
                        },
                        onNavigateToNotifications = {
                            closeDrawer()
                            navController.navigate(NavigationRoutes.Notifications.route)
                        },
                        onLogout = {
                            closeDrawer()
                            sideMenuViewModel.onAction(SideMenuAction.Logout)
                            navController.navigate(NavigationRoutes.Auth.route) {
                                popUpTo(NavigationRoutes.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }
            }
            
            // Top right menu button (only for authenticated screens)
            if (shouldShowDrawer) {
                FloatingActionButton(
                    onClick = openDrawer,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(56.dp),
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open menu",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // Bottom navigation (only for authenticated screens)
            if (shouldShowDrawer) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigateToBusinessUnit = {
                            navController.navigate(NavigationRoutes.BusinessUnitLanding.route) {
                                popUpTo(NavigationRoutes.BusinessUnitLanding.route) { inclusive = true }
                            }
                        },
                        onNavigateToClockIn = {
                            navController.navigate(NavigationRoutes.Welcome.route)
                        },
                        onNavigateToSchedule = {
                            navController.navigate(NavigationRoutes.WeeklySchedule.route)
                        }
                    )
                }
            }
        }
    }
}

// Wrapper screens with drawer support
@Composable
private fun WelcomeScreenWithDrawer(
    viewModel: WelcomeViewModel,
    onOpenDrawer: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Clock In") },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            elevation = 8.dp
        )
        // Original welcome screen content
        WelcomeScreenRoot(viewModel = viewModel)
    }
}

@Composable
private fun WeeklyScheduleScreenWithDrawer(
    viewModel: WeeklyScheduleViewModel,
    onOpenDrawer: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Weekly Schedule") },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            elevation = 8.dp
        )
        // Original schedule screen content
        WeeklyScheduleScreenRoot(viewModel = viewModel)
    }
}

@Composable
private fun CalendarScreenWithDrawer(
    viewModel: CalendarViewModel,
    onOpenDrawer: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Calendar") },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            elevation = 8.dp
        )
        // Original calendar screen content
        CalendarScreenRoot(viewModel = viewModel)
    }
}

@Composable
private fun ProfileScreenWithDrawer(
    viewModel: ProfileViewModel,
    navController: androidx.navigation.NavController,
    onOpenDrawer: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Profile") },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            elevation = 8.dp
        )
        ProfileScreenRoot(
            viewModel = viewModel,
            navController = navController
        )
    }
}

@Composable
private fun BusinessScreenWithDrawer(
    viewModel: BusinessViewModel,
    onNavigateToSearch: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Business Unit Management") },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            elevation = 8.dp
        )
        BusinessScreenRoot(
            viewModel = viewModel,
            onNavigateToSearch = onNavigateToSearch
        )
    }
}

@Composable
private fun SearchScreenWithDrawer(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Search Employees") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            elevation = 8.dp
        )
        SearchScreenRoot(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack
        )
    }
}

@Composable
private fun ShiftExchangeScreenWithDrawer(
    viewModel: ShiftExchangeViewModel,
    onOpenDrawer: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Shift Exchange") },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            elevation = 8.dp
        )
        ShiftExchangeScreenRoot(viewModel = viewModel)
    }
}

@Composable
private fun PostsScreenWithDrawer(
    viewModel: PostsViewModel,
    navController: androidx.navigation.NavController,
    onOpenDrawer: () -> Unit
) {
    PostsScreenRoot(
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
private fun NotificationsScreenWithDrawer(onOpenDrawer: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Notifications") },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            elevation = 8.dp
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.onSurface
                )
                Text(
                    text = "Coming soon...",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}