package com.clockwise.features.sidemenu.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.clockwise.features.sidemenu.presentation.components.BusinessUnitHeader
import com.clockwise.features.sidemenu.presentation.components.QuickActionButtons
import com.clockwise.features.sidemenu.presentation.components.BusinessUnitDetails

@Composable
fun BusinessUnitLandingScreen(
    state: SideMenuState,
    onAction: (SideMenuAction) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Ensure business unit data is loaded when screen is displayed
    LaunchedEffect(Unit) {
        if (state.businessUnit == null && state.error != null) {
            onAction(SideMenuAction.RefreshBusinessUnit)
        }
    }
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "ClockWise",
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(
                    onClick = { onAction(SideMenuAction.RefreshBusinessUnit) },
                    enabled = !state.isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            elevation = 8.dp
        )
        
        // Content
        when {
            state.isLoading -> {
                LoadingContent(modifier = Modifier.weight(1f))
            }
            state.error != null -> {
                ErrorContent(
                    error = state.error,
                    onRetryClick = { onAction(SideMenuAction.RefreshBusinessUnit) },
                    modifier = Modifier.weight(1f)
                )
            }
            state.businessUnit != null -> {
                BusinessUnitContent(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.weight(1f)
                )
            }
            else -> {
                ErrorContent(
                    error = "No business unit information available",
                    onRetryClick = { onAction(SideMenuAction.RefreshBusinessUnit) },
                    modifier = Modifier.weight(1f)
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
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
    state: SideMenuState,
    onAction: (SideMenuAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val businessUnit = state.businessUnit ?: return
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Section
        WelcomeSection(
            businessUnitName = businessUnit.name,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Business Unit Header
        BusinessUnitHeader(businessUnit = businessUnit)
        
        // Quick Action Buttons
        QuickActionButtons(
            businessUnit = businessUnit,
            onCallClick = { onAction(SideMenuAction.CallBusinessUnit) },
            onEmailClick = { onAction(SideMenuAction.EmailBusinessUnit) },
            onDirectionsClick = { onAction(SideMenuAction.GetDirections) }
        )
        
        // Business Unit Details
        BusinessUnitDetails(businessUnit = businessUnit)
        
        // Bottom spacer
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun WelcomeSection(
    businessUnitName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary
            )
            
            Text(
                text = "You're working at $businessUnitName",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
            )
            
            Text(
                text = "Here's everything you need to know about your workplace",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}