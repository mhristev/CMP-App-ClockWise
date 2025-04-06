package com.clockwise.user.presentation.home.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    navController: NavController? = null
) {
    LaunchedEffect(Unit) {
        onAction(ProfileAction.LoadUserProfile)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Profile Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4A2B8C)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile picture",
                        modifier = Modifier.size(60.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = state.userProfile?.name ?: "Loading...",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A2B8C)
                )

                Text(
                    text = state.userProfile?.email ?: "",
                    style = MaterialTheme.typography.body1,
                    color = Color(0xFF666666)
                )
            }
        }

        item {
            // Profile Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Profile Information",
                        style = MaterialTheme.typography.h6,
                        color = Color(0xFF4A2B8C),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Build,
                        label = "Role",
                        value = state.userProfile?.role ?: "Loading..."
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Build,
                        label = "Company",
                        value = state.userProfile?.company ?: "Loading..."
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Phone,
                        label = "Phone",
                        value = state.userProfile?.phone ?: "Not set"
                    )
                }
            }
        }

        item {
            // Settings
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.h6,
                        color = Color(0xFF4A2B8C),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    SettingsRow(
                        icon = Icons.Default.Notifications,
                        label = "Notifications",
                        onClick = { /* TODO: Navigate to notifications settings */ }
                    )

                    SettingsRow(
                        icon = Icons.Default.Lock,
                        label = "Change Password",
                        onClick = { /* TODO: Navigate to password change */ }
                    )

                    SettingsRow(
                        icon = Icons.Default.Phone,
                        label = "Language",
                        onClick = { /* TODO: Navigate to language settings */ }
                    )

                    SettingsRow(
                        icon = Icons.Default.Phone,
                        label = "Dark Mode",
                        onClick = { /* TODO: Toggle dark mode */ }
                    )
                }
            }
        }

        item {
            // Logout Button
            Button(
                onClick = { 
                    onAction(ProfileAction.Logout)
                    navController?.navigate("auth") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF4A2B8C)
                )
            ) {
                Text("Logout", color = Color.White)
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4A2B8C),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.body2,
                color = Color(0xFF666666)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.body1,
                color = Color(0xFF333333)
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4A2B8C),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.body1,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null,
            tint = Color(0xFF666666)
        )
    }
}

data class UserProfile(
    val name: String,
    val email: String,
    val role: String,
    val company: String,
    val phone: String? = null
)

sealed interface ProfileAction {
    object LoadUserProfile : ProfileAction
    data class UpdateProfile(val profile: UserProfile) : ProfileAction
    object Logout : ProfileAction
}

data class ProfileState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = true
) 