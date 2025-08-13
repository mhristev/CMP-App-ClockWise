package com.clockwise.features.sidemenu.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.clockwise.core.model.UserRole
import com.clockwise.features.sidemenu.presentation.components.BusinessUnitHeader
import com.clockwise.features.sidemenu.presentation.components.QuickActionButtons
import com.clockwise.features.sidemenu.presentation.components.NavigationActions

@Composable
fun BusinessUnitDashboard(
    state: SideMenuState,
    userRole: UserRole?,
    onAction: (SideMenuAction) -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToEmployeeList: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isMenuOpen) {
        Dialog(
            onDismissRequest = { onAction(SideMenuAction.CloseMenu) },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .semantics {
                        contentDescription = "Business Unit Dashboard Dialog"
                    },
                elevation = 8.dp,
                backgroundColor = MaterialTheme.colors.background
            ) {
                BusinessUnitDashboardContent(
                    state = state,
                    userRole = userRole,
                    onAction = onAction,
                    onNavigateToSchedule = {
                        onNavigateToSchedule()
                        onAction(SideMenuAction.CloseMenu)
                    },
                    onNavigateToEmployeeList = {
                        onNavigateToEmployeeList()
                        onAction(SideMenuAction.CloseMenu)
                    },
                    onNavigateToSettings = {
                        onNavigateToSettings()
                        onAction(SideMenuAction.CloseMenu)
                    }
                )
            }
        }
    }
}

@Composable
private fun BusinessUnitDashboardContent(
    state: SideMenuState,
    userRole: UserRole?,
    onAction: (SideMenuAction) -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToEmployeeList: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header with close button
        DashboardHeader(
            onCloseClick = { onAction(SideMenuAction.CloseMenu) },
            onRefreshClick = { onAction(SideMenuAction.RefreshBusinessUnit) },
            isLoading = state.isLoading
        )
        
        // Content
        when {
            state.isLoading -> {
                LoadingContent()
            }
            state.error != null -> {
                ErrorContent(
                    error = state.error,
                    onRetryClick = { onAction(SideMenuAction.RefreshBusinessUnit) }
                )
            }
            state.businessUnit != null -> {
                BusinessUnitContent(
                    businessUnit = state.businessUnit,
                    userRole = userRole,
                    onAction = onAction,
                    onNavigateToSchedule = onNavigateToSchedule,
                    onNavigateToEmployeeList = onNavigateToEmployeeList,
                    onNavigateToSettings = onNavigateToSettings,
                    modifier = Modifier.weight(1f)
                )
            }
            else -> {
                ErrorContent(
                    error = "No business unit information available",
                    onRetryClick = { onAction(SideMenuAction.RefreshBusinessUnit) }
                )
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    onCloseClick: () -> Unit,
    onRefreshClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Business Unit Dashboard",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onSurface
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onRefreshClick,
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = if (isLoading) MaterialTheme.colors.onSurface.copy(alpha = 0.4f) 
                          else MaterialTheme.colors.onSurface
                )
            }
            
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.primary
            )
            Text(
                text = "Loading business unit information...",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Center
            )
            
            Button(
                onClick = onRetryClick,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Text(
                    text = "Retry",
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}

@Composable
private fun BusinessUnitContent(
    businessUnit: com.clockwise.features.organization.data.model.BusinessUnit,
    userRole: UserRole?,
    onAction: (SideMenuAction) -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToEmployeeList: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Business Unit Header
        BusinessUnitHeader(businessUnit = businessUnit)
        
        // Quick Action Buttons
        QuickActionButtons(
            businessUnit = businessUnit,
            onCallClick = { onAction(SideMenuAction.CallBusinessUnit) },
            onEmailClick = { onAction(SideMenuAction.EmailBusinessUnit) },
            onDirectionsClick = { onAction(SideMenuAction.GetDirections) }
        )
        
        // Navigation Actions
        NavigationActions(
            userRole = userRole,
            onNavigateToSchedule = onNavigateToSchedule,
            onNavigateToEmployeeList = onNavigateToEmployeeList,
            onNavigateToSettings = onNavigateToSettings
        )
        
        // Bottom spacer for better scrolling experience
        Spacer(modifier = Modifier.height(16.dp))
    }
}