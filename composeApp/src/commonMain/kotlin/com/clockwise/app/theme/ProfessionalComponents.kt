package com.clockwise.app.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Professional UI Components using the ClockWise color system
 */

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: Brush = ClockWiseGradients.PrimaryVertical,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = androidx.compose.ui.graphics.Color.Transparent,
            contentColor = ClockWiseTheme.colors.onPrimary
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        ),
        shape = ClockWiseComponentShapes.ButtonMedium,
        modifier = modifier
            .background(
                brush = if (enabled) gradient else ClockWiseGradients.LightBackground,
                shape = ClockWiseComponentShapes.ButtonMedium
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.button.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun ProfessionalCard(
    modifier: Modifier = Modifier,
    gradient: Brush = ClockWiseGradients.LightBackground,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = ClockWiseComponentShapes.Card
            ),
        shape = ClockWiseComponentShapes.Card,
        backgroundColor = androidx.compose.ui.graphics.Color.Transparent,
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = gradient,
                    shape = ClockWiseComponentShapes.Card
                )
                .padding(16.dp),
            content = content
        )
    }
}

@Composable
fun StatusBadge(
    text: String,
    type: StatusType = StatusType.Info,
    modifier: Modifier = Modifier
) {
    val colors = when (type) {
        StatusType.Success -> ClockWiseTheme.colors.success to ClockWiseTheme.colors.onSuccess
        StatusType.Warning -> ClockWiseTheme.colors.warning to ClockWiseTheme.colors.onWarning
        StatusType.Error -> ClockWiseTheme.colors.error to ClockWiseTheme.colors.onError
        StatusType.Info -> ClockWiseTheme.colors.info to ClockWiseTheme.colors.onInfo
    }
    
    Box(
        modifier = modifier
            .background(
                color = colors.first,
                shape = ClockWiseComponentShapes.Badge
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = ClockWiseTextStyles.Badge,
            color = colors.second
        )
    }
}

enum class StatusType {
    Success, Warning, Error, Info
}

@Composable
fun FeatureCard(
    title: String,
    subtitle: String,
    featureType: FeatureType,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val gradient = when (featureType) {
        FeatureType.Shift -> ClockWiseGradients.ShiftGradient
        FeatureType.TimeTracking -> ClockWiseGradients.TimeTrackingGradient
        FeatureType.Profile -> ClockWiseGradients.ProfileGradient
        FeatureType.Business -> ClockWiseGradients.BusinessGradient
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, ClockWiseComponentShapes.Card),
        shape = ClockWiseComponentShapes.Card,
        backgroundColor = androidx.compose.ui.graphics.Color.Transparent,
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .background(gradient)
                .padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = ClockWiseTheme.colors.onPrimary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.body2,
                color = ClockWiseTheme.colors.onPrimary.copy(alpha = 0.9f)
            )
        }
    }
}

enum class FeatureType {
    Shift, TimeTracking, Profile, Business
}