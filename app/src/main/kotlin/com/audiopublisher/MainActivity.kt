package com.audiopublisher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.audiopublisher.ui.AudioPublisherNavHost
import com.audiopublisher.ui.theme.AudioPublisherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioPublisherTheme {
                AudioPublisherNavHost()
            }
        }
    }
}
