package com.example.jeeps.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeps.ui.components.FlagStripe
import com.example.jeeps.ui.theme.*

@Composable
fun SettingsScreen(
    onBack       : () -> Unit = {},
    lang         : String     = "EN",
    onLangChange : (String) -> Unit = {},
    darkMode     : Boolean    = false,
    onDarkChange : (Boolean) -> Unit = {},
) {
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
                    text       = "Settings",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = "App preferences",
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

        LazyColumn(
            modifier            = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding      = PaddingValues(bottom = 24.dp),
        ) {
            item { SettingsSectionLabel("Display") }
            item {
                ToggleSettingRow(
                    label           = "Dark Mode",
                    sublabel        = if (darkMode) "On" else "Off",
                    checked         = darkMode,
                    onCheckedChange = onDarkChange,
                    surface         = surface,
                    outline         = outline,
                    onSurface       = onSurf,
                )
            }
            item {
                Spacer(Modifier.height(4.dp))
                SettingsSectionLabel("Language")
            }
            item {
                SegmentedLanguageRow(
                    selected = lang,
                    onSelect = onLangChange,
                    surface  = surface,
                    outline  = outline,
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionLabel(title: String) {
    Text(
        text          = title.uppercase(),
        fontSize      = 10.sp,
        fontWeight    = FontWeight.Bold,
        color         = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        modifier      = Modifier.padding(start = 4.dp, bottom = 2.dp),
        letterSpacing = 1.2.sp,
    )
}

@Composable
private fun ToggleSettingRow(
    label           : String,
    sublabel        : String,
    checked         : Boolean,
    onCheckedChange : (Boolean) -> Unit,
    surface         : Color,
    outline         : Color,
    onSurface       : Color,
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(surface)
            .border(1.5.dp, outline, RoundedCornerShape(14.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text       = label,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                color      = onSurface,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text     = sublabel,
                fontSize = 11.sp,
                color    = onSurface.copy(alpha = 0.5f),
            )
        }
        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            colors          = SwitchDefaults.colors(
                checkedThumbColor   = Color.White,
                checkedTrackColor   = PrimaryBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = outline,
            )
        )
    }
}

@Composable
private fun SegmentedLanguageRow(
    selected : String,
    onSelect : (String) -> Unit,
    surface  : Color,
    outline  : Color,
) {
    val options = listOf("EN" to "English", "TL" to "Tagalog")

    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(surface)
            .border(1.5.dp, outline, RoundedCornerShape(14.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (code, label) ->
            val isSelected = selected == code
            val textColor  = if (isSelected) Color.White
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) PrimaryBlue else Color.Transparent)
                    .clickable { onSelect(code) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = code,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = textColor,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text     = label,
                        fontSize = 10.sp,
                        color    = textColor.copy(alpha = if (isSelected) 0.75f else 1f),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun SettingsScreenPreview() {
    var lang     by remember { mutableStateOf("EN") }
    var darkMode by remember { mutableStateOf(false) }
    JeePSTheme(darkTheme = darkMode) {
        SettingsScreen(
            lang         = lang,
            onLangChange = { lang = it },
            darkMode     = darkMode,
            onDarkChange = { darkMode = it },
        )
    }
}