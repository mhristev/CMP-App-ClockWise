package com.clockwise.features.sidemenu.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.clockwise.core.model.User
import com.clockwise.core.model.UserRole

@Composable
fun DrawerContent(
    currentUser: User?,
    currentRoute: String?,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToClockIn: () -> Unit,
    onNavigateToBusinessUnit: () -> Unit,
    onNavigateToShiftExchange: () -> Unit,
    onNavigateToPosts: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp),
        color = MaterialTheme.colors.surface,
        elevation = 16.dp
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // User Profile Header
            item {
                DrawerHeader(
                    user = currentUser,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Navigation Items
            item {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    DrawerMenuItem(
                        icon = Icons.Default.Home,
                        title = "Home",
                        isSelected = currentRoute == "businessunit_landing",
                        onClick = onNavigateToHome
                    )
                    
                    DrawerMenuItem(
                        icon = Icons.Default.Person,
                        title = "My Profile",
                        isSelected = currentRoute == "profile",
                        onClick = onNavigateToProfile
                    )
                    
                    DrawerMenuItem(
                        icon = Icons.Default.DateRange,
                        title = "Schedule",
                        isSelected = currentRoute == "weekly_schedule",
                        onClick = onNavigateToSchedule
                    )
                    
                    DrawerMenuItem(
                        icon = Icons.Default.CalendarToday,
                        title = "Calendar",
                        isSelected = currentRoute == "calendar",
                        onClick = onNavigateToCalendar
                    )
                    
                    DrawerMenuItem(
                        icon = Icons.Default.AccessTime,
                        title = "Clock In",
                        isSelected = currentRoute == "clock_in",
                        onClick = onNavigateToClockIn
                    )
                    
                    DrawerMenuItem(
                        icon = Icons.Default.SwapHoriz,
                        title = "Shift Exchange",
                        isSelected = currentRoute == "shift_exchange",
                        onClick = onNavigateToShiftExchange
                    )
                    
                    DrawerMenuItem(
                        icon = Icons.Default.Article,
                        title = "Posts",
                        isSelected = currentRoute == "posts",
                        onClick = onNavigateToPosts
                    )
                    
                    // Show Business Unit for Admin/Manager only
                    if (currentUser?.role == UserRole.ADMIN || currentUser?.role == UserRole.MANAGER) {
                        DrawerMenuItem(
                            icon = Icons.Default.Business,
                            title = "Business Unit",
                            isSelected = currentRoute == "business",
                            onClick = onNavigateToBusinessUnit
                        )
                    }
                    
                    DrawerMenuItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        isSelected = currentRoute == "notifications",
                        onClick = onNavigateToNotifications
                    )
                    
                    DrawerMenuItem(
                        icon = Icons.Default.Settings,
                        title = "Settings",
                        isSelected = currentRoute == "settings",
                        onClick = onNavigateToSettings
                    )
                }
            }
            
            // Spacer to push logout to bottom
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            // Logout
            item {
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
                
                DrawerMenuItem(
                    icon = Icons.Default.ExitToApp,
                    title = "Log Out",
                    isSelected = false,
                    onClick = onLogout,
                    tintColor = MaterialTheme.colors.error
                )
            }
        }
    }
}

@Composable
private fun DrawerHeader(
    user: User?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(160.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6B46C1), // Purple
                        Color(0xFF3B82F6)  // Blue
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // User Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // User Name
            Text(
                text = user?.let { "${it.firstName} ${it.lastName}" } ?: "User",
                style = MaterialTheme.typography.h6,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            // User Email
            Text(
                text = user?.email ?: "user@example.com",
                style = MaterialTheme.typography.body2,
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            // Business Unit (if available)
            user?.businessUnitName?.let { businessUnit ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = businessUnit,
                    style = MaterialTheme.typography.caption,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tintColor: Color? = null
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colors.primary.copy(alpha = 0.1f)
    } else {
        Color.Transparent
    }
    
    val contentColor = tintColor ?: if (isSelected) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.onSurface
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = contentColor
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}