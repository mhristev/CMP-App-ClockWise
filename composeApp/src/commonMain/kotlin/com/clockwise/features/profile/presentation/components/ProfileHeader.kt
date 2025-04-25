package com.clockwise.features.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clockwise.features.profile.domain.model.UserProfile
import com.clockwise.features.profile.presentation.theme.ProfileColors

/**
 * Header component displaying user avatar and basic information
 */
@Composable
fun ProfileHeader(
    userProfile: UserProfile?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(ProfileColors.Primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile picture",
                modifier = Modifier.size(60.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (userProfile != null) "${userProfile.firstName} ${userProfile.lastName}" else "Loading...",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = ProfileColors.Primary
        )

        userProfile?.email?.let { email ->
            Text(
                text = email,
                style = MaterialTheme.typography.body1,
                color = ProfileColors.TextSecondary
            )
        }
    }
} 