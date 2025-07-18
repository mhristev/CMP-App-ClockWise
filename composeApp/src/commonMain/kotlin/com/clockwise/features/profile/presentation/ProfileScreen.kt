package com.clockwise.features.profile.presentation

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
import androidx.navigation.NavController
import com.clockwise.app.navigation.NavigationRoutes
import com.clockwise.features.profile.presentation.components.ProfileHeader
import com.clockwise.features.profile.presentation.components.ProfileInfoRow
import com.clockwise.features.profile.presentation.components.SettingsRow
import com.clockwise.features.profile.presentation.theme.ProfileColors

@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    navController: NavController? = null,
    viewModel: ProfileViewModel? = null
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
            ProfileHeader(userProfile = state.userProfile)
        }

        item {
            // Profile Information Section
            ProfileInformationSection(state = state)
        }

        item {
            // Settings Section
            SettingsSection()
        }
        
        item {
            // GDPR Section
            GdprSection(onAction = onAction)
        }

        item {
            // Logout Button
            LogoutButton(
                onLogout = {
                    onAction(ProfileAction.Logout)
                    navController?.navigate(NavigationRoutes.Auth.route) {
                        popUpTo(0) // Clear entire back stack
                        launchSingleTop = true
                    }
                }
            )
        }
    }
    
    // Confirmation Dialog
    if (state.showAnonymizeConfirmation && viewModel != null) {
        AnonymizeAccountConfirmationDialog(
            onConfirm = {
                viewModel.confirmAnonymizeAccount()
                // Navigation is now handled via the redirectToAuth state in ProfileScreenRoot
            },
            onDismiss = { viewModel.hideAnonymizeConfirmation() }
        )
    }
}

@Composable
private fun ProfileInformationSection(state: ProfileState) {
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
                color = ProfileColors.Primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ProfileInfoRow(
                icon = Icons.Default.Person,
                label = "First Name",
                value = state.userProfile?.firstName ?: "Loading..."
            )

            ProfileInfoRow(
                icon = Icons.Default.Person,
                label = "Last Name",
                value = state.userProfile?.lastName ?: "Loading..."
            )

            ProfileInfoRow(
                icon = Icons.Default.Email,
                label = "Email",
                value = state.userProfile?.email ?: "Loading..."
            )

            ProfileInfoRow(
                icon = Icons.Default.Phone,
                label = "Role",
                value = state.userProfile?.role ?: "Loading..."
            )

            ProfileInfoRow(
                icon = Icons.Default.Email,
                label = "Company",
                value = state.userProfile?.company ?: "Loading..."
            )

            ProfileInfoRow(
                icon = Icons.Default.Phone,
                label = "Phone Number",
                value = state.userProfile?.phoneNumber ?: "Not set"
            )
        }
    }
}

@Composable
private fun SettingsSection() {
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
                color = ProfileColors.Primary,
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
                icon = Icons.Default.Person,
                label = "Language",
                onClick = { /* TODO: Navigate to language settings */ }
            )

            SettingsRow(
                icon = Icons.Default.Person,
                label = "Dark Mode",
                onClick = { /* TODO: Toggle dark mode */ }
            )
        }
    }
}

@Composable
private fun GdprSection(onAction: (ProfileAction) -> Unit) {
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
                text = "Privacy & GDPR",
                style = MaterialTheme.typography.h6,
                color = ProfileColors.Primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Your data privacy rights under GDPR",
                style = MaterialTheme.typography.body2,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Button(
                onClick = { onAction(ProfileAction.AnonymizeAccount) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete My Account")
            }
        }
    }
}

@Composable
private fun AnonymizeAccountConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Account") },
        text = { 
            Column {
                Text("Are you sure you want to delete your account?")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "This will anonymize your personal data in accordance with GDPR requirements. This action cannot be undone.",
                    style = MaterialTheme.typography.body2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Text("Yes, Delete My Account", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    Button(
        onClick = onLogout,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = ProfileColors.Primary,
            contentColor = Color.White
        )
    ) {
        Text("Logout", color = Color.White)
    }
}
