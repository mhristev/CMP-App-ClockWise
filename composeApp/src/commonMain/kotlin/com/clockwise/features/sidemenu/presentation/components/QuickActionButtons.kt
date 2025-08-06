package com.clockwise.features.sidemenu.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clockwise.features.organization.data.model.BusinessUnit

@Composable
fun QuickActionButtons(
    businessUnit: BusinessUnit,
    onCallClick: () -> Unit,
    onEmailClick: () -> Unit,
    onDirectionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 1.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.onSurface
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Call Button
                if (!businessUnit.phoneNumber.isNullOrBlank()) {
                    QuickActionButton(
                        icon = Icons.Default.Call,
                        label = "Call",
                        onClick = onCallClick,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    QuickActionButton(
                        icon = Icons.Default.Call,
                        label = "Call",
                        onClick = { },
                        enabled = false,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Email Button
                if (!businessUnit.email.isNullOrBlank()) {
                    QuickActionButton(
                        icon = Icons.Default.Email,
                        label = "Email",
                        onClick = onEmailClick,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    QuickActionButton(
                        icon = Icons.Default.Email,
                        label = "Email",
                        onClick = { },
                        enabled = false,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Directions Button
                QuickActionButton(
                    icon = Icons.Default.DirectionsWalk,
                    label = "Directions",
                    onClick = onDirectionsClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (enabled) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Medium
            )
        }
    }
}