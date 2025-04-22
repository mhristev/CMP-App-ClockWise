package com.clockwise.app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clockwise.app.navigation.NavigationRoutes
import org.koin.compose.viewmodel.koinViewModel
import com.clockwise.features.welcome.presentation.HomeScreenRoot
import com.clockwise.features.welcome.presentation.HomeViewModel
import com.clockwise.core.UserService
import org.koin.compose.koinInject
import com.clockwise.features.welcome.presentation.HomeAction

import com.clockwise.features.auth.presentation.AuthScreenRoot
import com.clockwise.features.auth.presentation.AuthViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val userService = koinInject<UserService>()
        NavHost(navController = navController, startDestination = NavigationRoutes.Register.route) {
            composable(NavigationRoutes.Register.route) {
                val viewModel = koinViewModel<AuthViewModel>()
                AuthScreenRoot(
                    viewModel = viewModel,
                    navController = navController
                )
            }
            composable(NavigationRoutes.Auth.route) {
                val viewModel = koinViewModel<AuthViewModel>()
                AuthScreenRoot(
                    viewModel = viewModel,
                    navController = navController
                )
            }
            composable(NavigationRoutes.Home.route) {
                val viewModel = koinViewModel<HomeViewModel>()
                HomeScreenRoot(
                    viewModel = viewModel,
                    onNavigate = { screen -> 
                        viewModel.onAction(HomeAction.NavigateToScreen(screen))
                    },
                    navController = navController,
                    userService = userService
                )
            }
        }
    }
}

//@Composable
//fun HomeScreen(viewModel: RegisterViewModel) {
//    val companies = viewModel.companies
//
//    LaunchedEffect(Unit) {
//        viewModel.fetchCompanies()
//    }
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Top
//    ) {
//        Text("Companies", style = MaterialTheme.typography.h4)
//        Spacer(modifier = Modifier.height(16.dp))
//        LazyColumn {
//            items(companies) { company ->
//                Text(company.name, style = MaterialTheme.typography.body1)
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//        }
//    }
//}
//
//
//
//@Serializable
//data class Company(
//    val id: String,
//    val name: String
//)


