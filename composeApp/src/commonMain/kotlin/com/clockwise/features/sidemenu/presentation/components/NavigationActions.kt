package com.clockwise.features.sidemenu.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clockwise.core.model.UserRole

@Composable
fun NavigationActions(
    userRole: UserRole?,
    onNavigateToSchedule: () -> Unit,
    onNavigateToEmployeeList: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 1.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Navigate To",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            NavigationActionItem(
                icon = Icons.Default.Schedule,
                title = "Schedule",
                subtitle = "View your weekly schedule",
                onClick = onNavigateToSchedule
            )
            
            // Show employee list for Admins and Managers only
            if (userRole == UserRole.ADMIN || userRole == UserRole.MANAGER) {
                NavigationActionItem(
                    icon = Icons.Default.People,
                    title = "Employee List",
                    subtitle = "Manage team members",
                    onClick = onNavigateToEmployeeList
                )
            }
            
            NavigationActionItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                subtitle = "App preferences and account",
                onClick = onNavigateToSettings
            )
        }
    }
}

@Composable
private fun NavigationActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}