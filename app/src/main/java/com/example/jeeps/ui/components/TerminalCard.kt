package com.example.jeeps.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeps.data.model.Terminal
import com.example.jeeps.ui.theme.*

@Composable
fun TerminalCard(
    terminal: Terminal,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        shape    = RoundedCornerShape(14.dp),
        color    = BgCard,
        modifier = modifier
            .width(148.dp)
            .border(1.5.dp, BorderLight, RoundedCornerShape(14.dp))
            .clickable { onClick() },
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(
                            color = if (terminal.isLow) StatusAmber else StatusGreen,
                            shape = CircleShape,
                        )
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text       = terminal.name,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 12.sp,
                    color      = TextDark,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text     = "${terminal.unitCount} units",
                fontSize = 11.sp,
                color    = TextSubtle,
            )
            Text(
                text     = terminal.routes.joinToString(" · "),
                fontSize = 10.sp,
                color    = TextHint,
            )
        }
    }
}