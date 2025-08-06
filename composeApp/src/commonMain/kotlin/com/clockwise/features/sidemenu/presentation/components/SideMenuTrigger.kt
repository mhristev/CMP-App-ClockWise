package com.clockwise.features.sidemenu.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun SideMenuTrigger(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.primary,
    contentColor: Color = MaterialTheme.colors.onPrimary
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(56.dp)
            .semantics {
                contentDescription = "Open Business Unit Dashboard"
                role = Role.Button
            },
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.Business,
            contentDescription = "Business Unit Dashboard",
            modifier = Modifier.size(24.dp)
        )
    }
}