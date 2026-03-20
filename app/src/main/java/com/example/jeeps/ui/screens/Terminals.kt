package com.example.jeeps.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jeeps.data.model.Terminal
import com.example.jeeps.ui.components.FlagStripe
import com.example.jeeps.ui.theme.*
import com.example.jeeps.ui.viewmodels.TerminalsViewModel

@Composable
fun TerminalsScreen(
    onBack    : () -> Unit = {},
    viewModel : TerminalsViewModel = viewModel(),
) {
    val uiState   by viewModel.uiState.collectAsStateWithLifecycle()
    val terminals : List<Terminal> = uiState.terminals

    val bg      = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.outline
    val onSurf  = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg),
    ) {
        FlagStripe()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryBlue),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 28.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier
                        .clickable { onBack() }
                        .padding(bottom = 10.dp),
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint               = Color.White.copy(alpha = 0.65f),
                        modifier           = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text     = "Back",
                        fontSize = 11.sp,
                        color    = Color.White.copy(alpha = 0.65f),
                    )
                }
                Text(
                    text       = "All Terminals",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = "${terminals.size} terminals · San Pedro to Calamba",
                    fontSize = 11.sp,
                    color    = Color.White.copy(alpha = 0.55f),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(bg),
            )
        }

        Row(
            modifier              = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            LegendDot(color = StatusGreen, label = "Normal",   textColor = onSurf.copy(alpha = 0.5f))
            LegendDot(color = StatusAmber, label = "Low units", textColor = onSurf.copy(alpha = 0.5f))
        }

        LazyColumn(
            modifier            = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding      = PaddingValues(bottom = 24.dp),
        ) {
            items(
                items = terminals,
                key   = { terminal -> terminal.id },
            ) { terminal ->
                TerminalRow(
                    terminal = terminal,
                    surface  = surface,
                    outline  = outline,
                    onSurf   = onSurf,
                )
            }
        }
    }
}

@Composable
private fun TerminalRow(
    terminal : Terminal,
    surface  : Color,
    outline  : Color,
    onSurf   : Color,
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(surface)
            .border(1.5.dp, outline, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier              = Modifier.weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = if (terminal.isLow) StatusAmber else StatusGreen,
                        shape = CircleShape,
                    )
            )
            Column {
                Text(
                    text       = terminal.name,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color      = onSurf,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text     = terminal.routes.joinToString(" · "),
                    fontSize = 11.sp,
                    color    = onSurf.copy(alpha = 0.5f),
                )
            }
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (terminal.isLow) YellowTint else BlueTint)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(
                text       = "${terminal.unitCount} units",
                fontSize   = 12.sp,
                fontWeight = FontWeight.Bold,
                color      = if (terminal.isLow) BayanBadgeText else BlueLight,
            )
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String, textColor: Color) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(modifier = Modifier.size(8.dp).background(color = color, shape = CircleShape))
        Text(text = label, fontSize = 11.sp, color = textColor)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun TerminalsScreenPreview() {
    TerminalsScreen()
}