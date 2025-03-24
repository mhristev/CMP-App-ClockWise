package com.clockwise.user.presentation.user_auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController


private val DarkPurple = Color(0xFF2D1B4E)
private val LightPurple = Color(0xFF4A2B8C)
private val Black = Color(0xFF121212)
private val White = Color(0xFFFFFFFF)

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
        onRegister = { email, username, password, confirmPassword ->
            viewModel.onAction(AuthAction.OnRegister(email, username, password, confirmPassword))
        },
        onLogin = { email, password ->
            viewModel.onAction(AuthAction.OnLogin(email, password))
        }
    )
}

@Composable
fun AuthScreen(
    state: AuthState,
    onRegister: (String, String, String, String) -> Unit,
    onLogin: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = if (isLoginMode) "Welcome Back" else "Create Account",
                style = MaterialTheme.typography.h4,
                color = White,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Mode Slider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkPurple),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isLoginMode) LightPurple else Color.Transparent)
                        .padding(vertical = 8.dp)
                        .clickable { isLoginMode = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Login",
                        color = White,
                        style = MaterialTheme.typography.button
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isLoginMode) LightPurple else Color.Transparent)
                        .padding(vertical = 8.dp)
                        .clickable { isLoginMode = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Register",
                        color = White,
                        style = MaterialTheme.typography.button
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Form Fields
            AnimatedVisibility(
                visible = !isLoginMode,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username", color = White) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = White,
                            cursorColor = LightPurple,
                            focusedBorderColor = LightPurple,
                            unfocusedBorderColor = DarkPurple
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = White) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = White,
                    cursorColor = LightPurple,
                    focusedBorderColor = LightPurple,
                    unfocusedBorderColor = DarkPurple
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = White) },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = White,
                    cursorColor = LightPurple,
                    focusedBorderColor = LightPurple,
                    unfocusedBorderColor = DarkPurple
                ),
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = !isLoginMode,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password", color = White) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = White,
                            cursorColor = LightPurple,
                            focusedBorderColor = LightPurple,
                            unfocusedBorderColor = DarkPurple
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            Button(
                onClick = {
                    if (isLoginMode) {
                        onLogin(email, password)
                    } else {
                        onRegister(email, username, password, confirmPassword)
                    }
                },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = LightPurple,
                    disabledBackgroundColor = DarkPurple
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = if (isLoginMode) "Login" else "Register",
                        style = MaterialTheme.typography.button,
                        color = White
                    )
                }
            }

            // Result Message
            state.resultMessage?.let { message ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = if (message.contains("error", ignoreCase = true)) Color.Red else LightPurple
                )
            }
        }
    }
}

@Composable
private fun ModeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
//            .weight(weight = 1f, fill = true)
            .padding(horizontal = 4.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) LightPurple else Color.Transparent,
            contentColor = White
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Text(text)
    }
}