package com.example.jeeps.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeps.ui.theme.AccentYellow
import com.example.jeeps.ui.theme.PrimaryBlue

@Composable
fun HeaderSection(
    lang: String,
    onLangChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = PrimaryBlue,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            )
            .padding(horizontal = 24.dp, vertical = 20.dp),
    ) {
        // Decorative ring
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-50).dp)
                .clip(CircleShape)
                .border(28.dp, Color.White.copy(alpha = 0.06f), CircleShape)
        )

        Column {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text(
                    text          = "JeePS · PH",
                    color         = Color.White.copy(alpha = 0.5f),
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                LanguageToggle(lang = lang, onLangChange = onLangChange)
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text       = if (lang == "EN") "Where are you\ngoing?" else "Saan ka\npupunta ngayon?",
                color      = Color.White,
                fontSize   = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 34.sp,
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint               = AccentYellow,
                    modifier           = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text     = if (lang == "EN")
                        "Calamba, Laguna · Location detected"
                    else
                        "Calamba, Laguna · Na-detect ang lokasyon",
                    color    = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                )
            }
        }
    }
}