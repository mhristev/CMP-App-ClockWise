package com.clockwise

import androidx.compose.ui.window.ComposeUIViewController
import com.clockwise.app.App
import com.clockwise.core.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }