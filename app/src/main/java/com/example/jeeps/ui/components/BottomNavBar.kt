package com.example.jeeps.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeps.ui.theme.*

@Composable
fun BottomNavBar(
    lang: String,
    selectedIndex: Int = 0,
    onItemSelected: (Int) -> Unit = {},
) {
    NavigationBar(
        containerColor = BgCard,
        tonalElevation = 0.dp,
    ) {
        val items = listOf(
            Pair(Icons.Default.Home,     if (lang == "EN") "Home"    else "Tahanan"),
            Pair(Icons.Default.Search,   if (lang == "EN") "Explore" else "Tuklasin"),
            Pair(Icons.Default.Place,    if (lang == "EN") "Map"     else "Mapa"),
            Pair(Icons.Default.Settings, "Settings"),
        )
        items.forEachIndexed { index, (icon, label) ->
            val selected = index == selectedIndex
            NavigationBarItem(
                selected = selected,
                onClick  = { onItemSelected(index) },
                label    = {
                    Text(
                        text       = label,
                        fontSize   = 9.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    )
                },
                icon   = { Icon(icon, contentDescription = label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = BlueLight,
                    selectedTextColor   = BlueLight,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor      = BlueTint,
                ),
            )
        }
    }
}