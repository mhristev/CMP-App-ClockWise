package com.clockwise.features.collaboration.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clockwise.features.collaboration.domain.model.Post
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun PostDetailModal(
    post: Post,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Full screen modal with dark background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopAppBar(
                    title = { Text("Post Details") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    },
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary,
                    elevation = 0.dp
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                TargetAudienceBadge(
                    targetAudience = post.targetAudience
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                InfoRow(
                    icon = Icons.Default.Person,
                    label = "Author",
                    value = post.authorFullName
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                InfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Created",
                    value = formatDateTime(post.createdAt)
                )
                
                if (post.createdAt != post.updatedAt) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    InfoRow(
                        icon = Icons.Default.Edit,
                        label = "Updated",
                        value = formatDateTime(post.updatedAt)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                InfoRow(
                    icon = Icons.Default.Group,
                    label = "Target Audience",
                    value = getTargetAudienceDisplayName(post.targetAudience)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.8f),
                    elevation = 2.dp
                ) {
                    Text(
                        text = post.body,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun TargetAudienceBadge(
    targetAudience: Post.TargetAudience,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (targetAudience) {
        Post.TargetAudience.ALL_EMPLOYEES -> "All Employees" to MaterialTheme.colors.primary
        Post.TargetAudience.MANAGERS_ONLY -> "Managers Only" to MaterialTheme.colors.secondary
        Post.TargetAudience.DEPARTMENT_ONLY -> "Department" to MaterialTheme.colors.primaryVariant
        Post.TargetAudience.TEAM_SPECIFIC -> "Team" to MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
    }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(end = 12.dp)
        )
        
        Text(
            text = "$label:",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface
        )
    }
}

private fun formatDateTime(timestamp: kotlinx.datetime.Instant): String {
    val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.monthNumber}/${localDateTime.dayOfMonth}/${localDateTime.year} at ${
        localDateTime.hour.toString().padStart(2, '0')
    }:${localDateTime.minute.toString().padStart(2, '0')}"
}

private fun getTargetAudienceDisplayName(targetAudience: Post.TargetAudience): String {
    return when (targetAudience) {
        Post.TargetAudience.ALL_EMPLOYEES -> "All Employees"
        Post.TargetAudience.MANAGERS_ONLY -> "Managers Only"
        Post.TargetAudience.DEPARTMENT_ONLY -> "Department Only"
        Post.TargetAudience.TEAM_SPECIFIC -> "Team Specific"
    }
}