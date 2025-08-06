package com.clockwise.features.sidemenu.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clockwise.features.organization.data.model.BusinessUnit

@Composable
fun BusinessUnitDetails(
    businessUnit: BusinessUnit,
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
                text = "Details",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.onSurface
            )
            
            // Location Details
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.onSurface
                    )
                    Text(
                        text = businessUnit.location,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Allowed Radius
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Radius",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "Clock-in Radius",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.onSurface
                    )
                    Text(
                        text = "${businessUnit.allowedRadius.toInt()}m from location",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Contact Information
            if (!businessUnit.phoneNumber.isNullOrBlank() || !businessUnit.email.isNullOrBlank()) {
                Divider(
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f),
                    thickness = 0.5.dp
                )
                
                Text(
                    text = "Contact Information",
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.onSurface
                )
                
                if (!businessUnit.phoneNumber.isNullOrBlank()) {
                    Text(
                        text = "Phone: ${businessUnit.phoneNumber}",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                if (!businessUnit.email.isNullOrBlank()) {
                    Text(
                        text = "Email: ${businessUnit.email}",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}