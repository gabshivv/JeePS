package com.example.jeeps.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jeeps.ui.theme.AccentYellow
import com.example.jeeps.ui.theme.PrimaryRed

@Composable
fun FlagStripe() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(4.dp)
    ) {
        // Maganda ba lighter blue? Para lang visible dun sa bg
        Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFF4A90D9)))
        Box(Modifier.weight(1f).fillMaxHeight().background(PrimaryRed))
        Box(Modifier.weight(1f).fillMaxHeight().background(AccentYellow))
    }
}