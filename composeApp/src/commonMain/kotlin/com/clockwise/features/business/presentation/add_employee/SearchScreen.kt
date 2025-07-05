package com.clockwise.features.business.presentation.add_employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clockwise.core.model.User
import kotlinx.serialization.Serializable

@Composable
fun SearchScreen(
    state: SearchState,
    onAction: (SearchAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showConfirmationDialog by remember { mutableStateOf<User?>(null) }
    
    // Handle success/error messages with LaunchedEffect
    LaunchedEffect(state.successMessage, state.error) {
        if (state.successMessage != null || state.error != null) {
            // Clear messages after 3 seconds
            kotlinx.coroutines.delay(3000)
            onAction(SearchAction.ClearMessages)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF4A2B8C)
                )
            }
            
            Text(
                text = "Search Employees",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A2B8C)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                onAction(SearchAction.Search(it))
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by username or email") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF4A2B8C),
                unfocusedBorderColor = Color(0xFF666666),
                cursorColor = Color(0xFF4A2B8C)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        // Success or error message
        if (state.successMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFF4CAF50),
                elevation = 4.dp
            ) {
                Text(
                    text = state.successMessage,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        } else if (state.error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFFF44336),
                elevation = 4.dp
            ) {
                Text(
                    text = state.error,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Loading and Results Section
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF4A2B8C),
                        modifier = Modifier.size(48.dp)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = state.searchResults,
                        key = { user -> user.id } // Use unique ID as key for proper animations
                    ) { user ->
                        UserCard(
                            user = user,
                            onAddToBusinessUnit = { showConfirmationDialog = it }
                        )
                    }
                    
                    // Show empty state message if there are no results
                    if (state.searchResults.isEmpty() && !state.isLoading && searchQuery.isNotBlank()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No users found",
                                    style = MaterialTheme.typography.subtitle1,
                                    color = Color(0xFF666666)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Confirmation Dialog
    showConfirmationDialog?.let { user ->
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = null },
            title = { Text("Add User to Business Unit") },
            text = { 
                Text("Are you sure you want to add ${user.firstName} ${user.lastName} to your business unit?") 
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAction(SearchAction.AddUserToBusinessUnit(user))
                        showConfirmationDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF4A2B8C)
                    )
                ) {
                    Text("Yes", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showConfirmationDialog = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun UserCard(user: User, onAddToBusinessUnit: (User) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${user.firstName} ${user.lastName}",
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A2B8C)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.body1,
                        color = Color(0xFF666666)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Role: ${user.role}",
                        style = MaterialTheme.typography.body2,
                        color = Color(0xFF666666)
                    )
                    
                    user.businessUnitName?.let { businessUnitName ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Business Unit: $businessUnitName",
                            style = MaterialTheme.typography.body2,
                            color = Color(0xFF666666)
                        )
                    }
                }
                
                // Only show the add button if the user isn't already in a business unit
                if (user.businessUnitId == null) {
                    IconButton(
                        onClick = { onAddToBusinessUnit(user) },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF4A2B8C), shape = MaterialTheme.shapes.small)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add to Business Unit",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}