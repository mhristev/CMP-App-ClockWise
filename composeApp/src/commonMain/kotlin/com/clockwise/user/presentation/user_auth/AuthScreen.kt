package com.clockwise.user.presentation.user_auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController


@Composable
fun AuthScreenRoot(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if(state.isAuthenticated) {
        LaunchedEffect(Unit) {
            navController.navigate("home") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    AuthScreen(
        state = state,
        onRegister = { email, password, confirmPassword ->
            viewModel.onAction(AuthAction.OnRegister(email, password, confirmPassword))
        },
        onLogin = { email, password ->
            viewModel.onAction(AuthAction.OnLogin(email, password))
        }
    )
}

@Composable
fun AuthScreen(
    state: AuthState,
    onRegister: (String, String, String) -> Unit,
    onLogin: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onRegister(email, password, confirmPassword) },
            enabled = !state.isLoading
        ) {
            Text(if (state.isLoading) "Loading..." else "Register")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onLogin(email, password) },
            enabled = !state.isLoading
        ) {
            Text(if (state.isLoading) "Loading..." else "Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        state.resultMessage?.let { message ->
            Text(message)
        }
    }
}