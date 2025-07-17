package com.clockwise.features.shift.presentation.welcome_shifts

import androidx.compose.runtime.*

@Composable
actual fun WelcomeScreenWithPermissions(
    state: WelcomeState,
    onAction: (WelcomeAction) -> Unit
) {
    // For iOS, use the same screen but handle permissions differently
    // iOS permission handling would go here in the future
    WelcomeScreen(
        state = state,
        onAction = onAction
    )
}
