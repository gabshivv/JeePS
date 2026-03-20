package com.example.jeeps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.jeeps.navigation.JeePSNavGraph
import com.example.jeeps.ui.theme.JeePSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkMode     by remember { mutableStateOf(false) }
            var showSettings by remember { mutableStateOf(false) }

            JeePSTheme(darkTheme = darkMode) {
                JeePSNavGraph(
                    darkMode      = darkMode,
                    onDarkChange  = { darkMode = it },
                    showSettings  = showSettings,
                    onShowSettings = { showSettings = it },
                )
            }
        }
    }
}