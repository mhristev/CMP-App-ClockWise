package com.clockwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.clockwise.app.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()

        setContent {
            App()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clean up any resources if needed
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}