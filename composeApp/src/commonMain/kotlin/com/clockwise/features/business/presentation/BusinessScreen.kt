package com.clockwise.features.business.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val Purple = Color(0xFF4A2B8C)
private val LightPurple = Color(0xFF6B4BAE)
private val White = Color(0xFFFFFFFF)
private val Gray = Color(0xFFF5F5F5)
private val DarkGray = Color(0xFF666666)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BusinessScreen(
    state: BusinessState,
    onAction: (BusinessAction) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    LaunchedEffect(Unit) {
        onAction(BusinessAction.LoadBusinessData)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 20.dp)
    ) {
        // Header with business name and back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.currentView != BusinessView.OVERVIEW) {
                IconButton(
                    onClick = { onAction(BusinessAction.SwitchView(BusinessView.OVERVIEW)) },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Purple.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Purple,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = when (state.currentView) {
                    BusinessView.OVERVIEW -> state.businessUnitName.ifEmpty { "Loading..." }
                    BusinessView.EMPLOYEES -> "Employees"
                },
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = Purple
            )
        }
        
        // General Information Card (only show in overview)
        if (state.currentView == BusinessView.OVERVIEW) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 0.dp,
                shape = RoundedCornerShape(16.dp),
                backgroundColor = White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Business Unit Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = Purple.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Purple,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = state.businessUnitName,
                                style = MaterialTheme.typography.h6,
                                color = Purple,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Business Unit",
                                style = MaterialTheme.typography.body2,
                                color = DarkGray
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
//                    // Stats Row
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        StatItem(
//                            icon = Icons.Default.Person,
//                            value = state.employees.size.toString(),
//                            label = "Total Employees"
//                        )
//
//                        StatItem(
//                            icon = Icons.Default.Person,
//                            value = state.employees.count { it.role == "MANAGER" }.toString(),
//                            label = "Managers"
//                        )
//                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Navigation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 0.dp,
                shape = RoundedCornerShape(16.dp),
                backgroundColor = White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Management",
                        style = MaterialTheme.typography.h6,
                        color = Purple,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    NavigationButton(
                        icon = Icons.Default.Home,
                        label = "Overview",
                        isSelected = state.currentView == BusinessView.OVERVIEW,
                        onClick = { onAction(BusinessAction.SwitchView(BusinessView.OVERVIEW)) }
                    )
                    
                    Divider(
                        color = Color(0xFFEEEEEE),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    
                    NavigationButton(
                        icon = Icons.Default.Person,
                        label = "Employees",
                        isSelected = state.currentView == BusinessView.EMPLOYEES,
                        onClick = { onAction(BusinessAction.SwitchView(BusinessView.EMPLOYEES)) }
                    )
                    
                    Divider(
                        color = Color(0xFFEEEEEE),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    
                    NavigationButton(
                        icon = Icons.Default.PlayArrow,
                        label = "Add New Employee",
                        isSelected = false,
                        onClick = { onNavigateToSearch() }
                    )
                }
            }
        } else {
            // Employees List
            EmployeesList(state.employees)
        }
    }
}

@Composable
private fun NavigationButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                color = if (isSelected) Purple.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Purple else DarkGray,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.body1,
            color = if (isSelected) Purple else DarkGray
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = if (isSelected) Purple else DarkGray,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Purple.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Purple,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.h6,
            color = Purple,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.body2,
            color = DarkGray
        )
    }
}

@Composable
private fun EmployeesList(employees: List<Employee>) {
    if (employees.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "No employees found",
                style = MaterialTheme.typography.subtitle1,
                color = DarkGray
            )
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(employees) { employee ->
                EmployeeCard(employee)
            }
        }
    }
}

@Composable
private fun EmployeeCard(employee: Employee) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 0.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Purple.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Purple,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = employee.username,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    color = Purple
                )
                
                Text(
                    text = employee.email,
                    style = MaterialTheme.typography.body2,
                    color = DarkGray
                )
                
                Text(
                    text = "Role: ${employee.role}",
                    style = MaterialTheme.typography.caption,
                    color = DarkGray
                )
            }
        }
    }
} 