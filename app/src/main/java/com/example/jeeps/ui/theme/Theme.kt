package com.example.jeeps.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light mode
private val JeePSLightColorScheme = lightColorScheme(
    primary          = PrimaryBlue,
    onPrimary        = Color.White,
    primaryContainer = BlueLight,

    secondary        = AccentYellow,
    onSecondary      = FareText,

    tertiary         = PrimaryRed,
    onTertiary       = Color.White,

    error            = PrimaryRed,
    onError          = Color.White,

    background       = BgApp,
    onBackground     = TextDark,

    surface          = BgCard,
    onSurface        = TextDark,
    surfaceVariant   = BgInput,

    outline          = BorderLight,
    outlineVariant   = BorderMedium,
)

// Dark mode — softer whites to reduce eye strain
private val JeePSDarkColorScheme = darkColorScheme(
    primary          = Color(0xFF5B8DEF),
    onPrimary        = Color(0xFF00235A),

    primaryContainer = PrimaryBlue,

    secondary        = AccentYellow,
    onSecondary      = Color(0xFF3A2800),

    tertiary         = Color(0xFFFF6B6B),
    onTertiary       = Color(0xFF5A0010),

    error            = Color(0xFFFF6B6B),
    onError          = Color(0xFF5A0010),

    background       = Color(0xFF111827),
    onBackground     = Color(0xFFB8C4D8),

    surface          = Color(0xFF1C2840),
    onSurface        = Color(0xFFB8C4D8),

    surfaceVariant   = Color(0xFF1A2235),

    outline          = Color(0xFF2E3D5C),
    outlineVariant   = Color(0xFF243352),
)

@Composable
fun JeePSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content:   @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) JeePSDarkColorScheme else JeePSLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content,
    )
}