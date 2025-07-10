package com.clockwise.features.auth.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.clockwise.core.model.PrivacyConsent
import com.clockwise.core.config.AppConfig

private val DarkPurple = Color(0xFF2D1B4E)
private val LightPurple = Color(0xFF4A2B8C)
private val Orange = Color(0xFFFF6B35)
private val Black = Color(0xFF121212)
private val White = Color(0xFFFFFFFF)

@Composable
fun AuthScreen(
    state: AuthState,
    onAction: (AuthAction) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    
    // GDPR consent variables
    var marketingConsent by remember { mutableStateOf(false) }
    var analyticsConsent by remember { mutableStateOf(false) }
    var thirdPartyConsent by remember { mutableStateOf(false) }
    
    var isLoginMode by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    // Clear all form fields when user is not authenticated (e.g., after logout)
    LaunchedEffect(state.isAuthenticated) {
        if (!state.isAuthenticated) {
            email = ""
            password = ""
            confirmPassword = ""
            firstName = ""
            lastName = ""
            phoneNumber = ""
            marketingConsent = false
            analyticsConsent = false
            thirdPartyConsent = false
            isLoginMode = true // Reset to login mode
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        if (state.isAuthenticated && !state.hasBusinessUnit) {
            // Show message for users without a business unit
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = LightPurple
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "No Business Unit Assigned",
                    style = MaterialTheme.typography.h4,
                    color = White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "Your account has been created successfully, but you haven't been assigned to a business unit yet. Please contact your administrator to complete your account setup.",
                    style = MaterialTheme.typography.body1,
                    color = White,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                
                Button(
                    onClick = { onAction(AuthAction.Logout) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = LightPurple
                    )
                ) {
                    Text("Logout", color = White)
                }
            }
        } else {
            // Regular login/register form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(scrollState),
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

                // Development Mode Test Accounts (only in debug mode and login mode)
                if (AppConfig.IS_DEBUG_MODE && isLoginMode) {
                    var isTestAccountsExpanded by remember { mutableStateOf(false) }
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        backgroundColor = DarkPurple,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Header row with expand/collapse functionality
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isTestAccountsExpanded = !isTestAccountsExpanded },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🚀 Development Mode",
                                        style = MaterialTheme.typography.h6,
                                        color = Orange
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isTestAccountsExpanded) "▼" else "▶",
                                        style = MaterialTheme.typography.h6,
                                        color = Orange
                                    )
                                }
                            }
                            
                            Text(
                                text = "Tap to ${if (isTestAccountsExpanded) "hide" else "show"} test accounts",
                                style = MaterialTheme.typography.body2,
                                color = White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            
                            // Expandable test accounts section
                            AnimatedVisibility(
                                visible = isTestAccountsExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(
                                    modifier = Modifier.padding(top = 16.dp)
                                ) {
                                    Text(
                                        text = "Quick Login with Test Accounts:",
                                        style = MaterialTheme.typography.body2,
                                        color = White,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                    
                                    AppConfig.TestAccounts.ALL_TEST_ACCOUNTS.forEach { testAccount ->
                                        Button(
                                            onClick = {
                                                email = testAccount.email
                                                password = testAccount.password
                                                onAction(AuthAction.Login(testAccount.email, testAccount.password))
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = Orange,
                                                contentColor = Black
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = testAccount.displayName,
                                                style = MaterialTheme.typography.button
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

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

                // Form Fields for Registration
                AnimatedVisibility(
                    visible = !isLoginMode,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        // First Name
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("First Name", color = White) },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = White,
                                cursorColor = LightPurple,
                                focusedBorderColor = LightPurple,
                                unfocusedBorderColor = DarkPurple
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Last Name
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("Last Name", color = White) },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = White,
                                cursorColor = LightPurple,
                                focusedBorderColor = LightPurple,
                                unfocusedBorderColor = DarkPurple
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Phone Number
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Phone Number", color = White) },
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

                // Email field (for both login and register)
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
                        
                        // GDPR Consent Checkboxes
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Privacy Consent",
                            color = White,
                            style = MaterialTheme.typography.subtitle1,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Data Sharing Consent (Required)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = thirdPartyConsent,
                                onCheckedChange = { thirdPartyConsent = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = LightPurple,
                                    uncheckedColor = White
                                )
                            )
                            Text(
                                text = "I agree to the processing of my personal data as required by GDPR (required)",
                                color = White,
                                style = MaterialTheme.typography.body2,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        
                        // Marketing Consent (Optional)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = marketingConsent,
                                onCheckedChange = { marketingConsent = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = LightPurple,
                                    uncheckedColor = White
                                )
                            )
                            Text(
                                text = "I agree to receive marketing communications",
                                color = White,
                                style = MaterialTheme.typography.body2,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        
                        // Analytics Consent (Optional)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = analyticsConsent,
                                onCheckedChange = { analyticsConsent = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = LightPurple,
                                    uncheckedColor = White
                                )
                            )
                            Text(
                                text = "I agree to the use of analytics to improve the service",
                                color = White,
                                style = MaterialTheme.typography.body2,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Action Button
                Button(
                    onClick = {
                        if (isLoginMode) {
                            onAction(AuthAction.Login(email, password))
                        } else {
                            onAction(
                                AuthAction.Register(
                                    email = email,
                                    password = password,
                                    confirmPassword = confirmPassword,
                                    firstName = firstName,
                                    lastName = lastName,
                                    phoneNumber = phoneNumber,
                                    privacyConsent = PrivacyConsent(
                                        marketingConsent = marketingConsent,
                                        analyticsConsent = analyticsConsent,
                                        thirdPartyDataSharingConsent = thirdPartyConsent
                                    )
                                )
                            )
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
                        color = if (message.contains("error", ignoreCase = true) || 
                                   message.contains("failed", ignoreCase = true)) 
                                Color.Red else LightPurple
                    )
                }
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