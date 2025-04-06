package com.clockwise.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

import kotlinx.coroutines.IO
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.lazy.items
import com.clockwise.company.presentation.CompanyScreen
import com.clockwise.company.presentation.CompanyViewModel
import com.clockwise.user.presentation.home.HomeScreen
import com.clockwise.user.presentation.home.HomeScreenRoot
import com.clockwise.user.presentation.home.HomeViewModel
import com.clockwise.service.UserService
import org.koin.compose.koinInject

import com.clockwise.user.presentation.user_auth.AuthScreen
import com.clockwise.user.presentation.user_auth.AuthScreenRoot
import com.clockwise.user.presentation.user_auth.AuthViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val userService = koinInject<UserService>()
        NavHost(navController = navController, startDestination = "register") {
            composable("register") {
                val viewModel = koinViewModel<AuthViewModel>()
                AuthScreenRoot(
                    viewModel = viewModel,
                    navController = navController
                )
            }
            composable("home") {
                val viewModel = koinViewModel<HomeViewModel>()
                HomeScreenRoot(
                    viewModel = viewModel,
                    onNavigate = { },
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


